package com.healthunspoken.insightsapi.repository;

import com.healthunspoken.insightsapi.dto.SelectedCommentsRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SelectedCommentRepository {

  private final JdbcTemplate jdbcTemplate;
  private final String curationDatabase;
  private boolean tableReady = false;

  public SelectedCommentRepository(
      JdbcTemplate jdbcTemplate,
      @Value("${insights.mysql.curation-database}") String curationDatabase) {
    this.jdbcTemplate = jdbcTemplate;
    this.curationDatabase = curationDatabase;
  }

  public int save(
      SelectedCommentsRequest.SelectedCommentItem item,
      String selectedBy,
      String defaultIntendedUse,
      String defaultQualityLabel,
      String defaultReviewNote,
      String detectedLanguage) {
    ensureTable();

    String sourceDb = firstNonBlank(item.databaseName(), item.database());
    if (sourceDb == null) {
      sourceDb = "unknown";
    }
    String sourceHash = hash(sourceDb + "\n" + item.commentId() + "\n" + item.text());
    String intendedUse = firstNonBlank(item.intendedUse(), defaultIntendedUse);
    String qualityLabel = firstNonBlank(item.qualityLabel(), defaultQualityLabel);
    String reviewNote = firstNonBlank(item.reviewNote(), defaultReviewNote);

    String sql =
        """
        insert into %s.selected_health_comments (
          source_db,
          source_comment_id,
          source_comment_hash,
          video_id,
          video_title,
          channel_id,
          channel_title,
          author_display_name,
          comment_text,
          like_count,
          published_at,
          fetched_at_ist,
          detected_language,
          quality_label,
          intended_use,
          review_note,
          selected_by
        ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        on duplicate key update
          video_title = values(video_title),
          channel_title = values(channel_title),
          author_display_name = values(author_display_name),
          comment_text = values(comment_text),
          like_count = values(like_count),
          published_at = values(published_at),
          fetched_at_ist = values(fetched_at_ist),
          detected_language = values(detected_language),
          quality_label = values(quality_label),
          intended_use = values(intended_use),
          review_note = values(review_note),
          selected_by = values(selected_by),
          updated_at = current_timestamp
        """
            .formatted(safeIdentifier(curationDatabase));

    jdbcTemplate.update(
        sql,
        sourceDb,
        item.commentId(),
        sourceHash,
        item.videoId(),
        item.videoTitle(),
        item.channelId(),
        item.channelTitle(),
        item.authorDisplayName(),
        item.text(),
        item.likeCount(),
        item.publishedAt(),
        item.fetchedAtIst(),
        detectedLanguage,
        qualityLabel,
        intendedUse,
        reviewNote,
        selectedBy);

    return 1;
  }

  private void ensureTable() {
    if (tableReady) {
      return;
    }

    String database = safeIdentifier(curationDatabase);
    jdbcTemplate.execute("create schema if not exists " + database);
    jdbcTemplate.execute(
        """
        create table if not exists %s.selected_health_comments (
          id bigint primary key auto_increment,
          source_db varchar(64) not null,
          source_comment_id varchar(255) not null,
          source_comment_hash char(64) not null,
          video_id varchar(64),
          video_title text,
          channel_id varchar(128),
          channel_title text,
          author_display_name text,
          comment_text text not null,
          like_count bigint,
          published_at varchar(64),
          fetched_at_ist varchar(32),
          detected_language varchar(32) not null,
          quality_label varchar(64),
          intended_use varchar(64),
          review_note text,
          selected_by varchar(128),
          selected_at timestamp not null default current_timestamp,
          created_at timestamp not null default current_timestamp,
          updated_at timestamp not null default current_timestamp on update current_timestamp,
          unique key source_comment_unique (source_db, source_comment_id),
          unique key source_comment_hash_unique (source_comment_hash),
          key selected_at_idx (selected_at),
          key detected_language_idx (detected_language),
          key intended_use_idx (intended_use)
        )
        """
            .formatted(database));
    tableReady = true;
  }

  private String firstNonBlank(String first, String second) {
    if (first != null && !first.isBlank()) {
      return first;
    }
    if (second != null && !second.isBlank()) {
      return second;
    }
    return null;
  }

  private String hash(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 is not available", exception);
    }
  }

  private String safeIdentifier(String identifier) {
    if (!identifier.matches("[A-Za-z0-9_]+")) {
      throw new IllegalArgumentException("Unsafe database identifier");
    }
    return "`" + identifier + "`";
  }
}
