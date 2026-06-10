package com.healthunspoken.insightsapi.repository;

import com.healthunspoken.insightsapi.dto.CommentDatabase;
import com.healthunspoken.insightsapi.dto.CommentRow;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommentQueryRepository {

  private final JdbcTemplate jdbcTemplate;
  private final String mainDatabase;
  private final String healthDatabase;

  public CommentQueryRepository(
      JdbcTemplate jdbcTemplate,
      @Value("${insights.mysql.main-database}") String mainDatabase,
      @Value("${insights.mysql.health-database}") String healthDatabase) {
    this.jdbcTemplate = jdbcTemplate;
    this.mainDatabase = mainDatabase;
    this.healthDatabase = healthDatabase;
  }

  public List<CommentRow> findByFetchedDate(LocalDate dateIst, CommentDatabase database, int limit) {
    LocalDateTime startUtc = dateIst.atStartOfDay().minusHours(5).minusMinutes(30);
    LocalDateTime endUtc = dateIst.plusDays(1).atStartOfDay().minusHours(5).minusMinutes(30);

    List<String> databases = databasesFor(database);
    int perDatabaseLimit = Math.max(1, limit);
    List<CommentRow> rows = new ArrayList<>();

    for (String databaseName : databases) {
      rows.addAll(queryFetchedRows(databaseName, startUtc, endUtc, perDatabaseLimit, false));
    }

    return rows.stream()
        .sorted((left, right) -> nullSafeLong(right.likeCount()).compareTo(nullSafeLong(left.likeCount())))
        .limit(limit)
        .toList();
  }

  public List<CommentRow> findTopQuestionCandidates(
      LocalDate dateIst, CommentDatabase database, int limit) {
    LocalDateTime startUtc = dateIst.atStartOfDay().minusHours(5).minusMinutes(30);
    LocalDateTime endUtc = dateIst.plusDays(1).atStartOfDay().minusHours(5).minusMinutes(30);

    List<CommentRow> rows = new ArrayList<>();
    for (String databaseName : databasesFor(database)) {
      rows.addAll(queryFetchedRows(databaseName, startUtc, endUtc, Math.max(100, limit), true));
    }

    return rows.stream()
        .sorted((left, right) -> nullSafeLong(right.likeCount()).compareTo(nullSafeLong(left.likeCount())))
        .limit(limit)
        .toList();
  }

  private List<CommentRow> queryFetchedRows(
      String databaseName,
      LocalDateTime startUtc,
      LocalDateTime endUtc,
      int limit,
      boolean questionOnly) {
    String safeDatabase = safeIdentifier(databaseName);
    String questionWhere =
        questionOnly
            ? """
              and (
                c.text like '%?%'
                or lower(c.text) like '%what%'
                or lower(c.text) like '%why%'
                or lower(c.text) like '%how%'
                or lower(c.text) like '%can %'
                or lower(c.text) like '%should%'
                or lower(c.text) like '%is it%'
              )
              """
            : "";

    String sql =
        """
        select
          ? as database_name,
          c.comment_id,
          c.video_id,
          v.title as video_title,
          v.channel_id,
          ch.title as channel_title,
          c.text,
          c.like_count,
          c.published_at,
          c.fetched_at,
          date_format(convert_tz(c.fetched_at, '+00:00', '+05:30'), '%%Y-%%m-%%d %%H:%%i:%%s') as fetched_at_ist
        from %s.comments c
        left join %s.videos v on v.video_id = c.video_id
        left join %s.channels ch on ch.channel_id = v.channel_id
        where c.fetched_at >= ?
          and c.fetched_at < ?
          and c.text is not null
          and char_length(trim(c.text)) >= 20
          %s
        order by c.like_count desc, c.fetched_at desc
        limit ?
        """
            .formatted(safeDatabase, safeDatabase, safeDatabase, questionWhere);

    return jdbcTemplate.query(
        sql,
        (rs, rowNum) ->
            new CommentRow(
                rs.getString("database_name"),
                rs.getString("comment_id"),
                rs.getString("video_id"),
                rs.getString("video_title"),
                rs.getString("channel_id"),
                rs.getString("channel_title"),
                rs.getString("text"),
                rs.getObject("like_count", Long.class),
                rs.getString("published_at"),
                rs.getObject("fetched_at", LocalDateTime.class),
                rs.getString("fetched_at_ist")),
        databaseName,
        startUtc,
        endUtc,
        limit);
  }

  private List<String> databasesFor(CommentDatabase database) {
    return switch (database) {
      case MAIN -> List.of(mainDatabase);
      case HEALTH -> List.of(healthDatabase);
      case BOTH -> List.of(mainDatabase, healthDatabase);
    };
  }

  private String safeIdentifier(String identifier) {
    if (!identifier.matches("[A-Za-z0-9_]+")) {
      throw new IllegalArgumentException("Unsafe database identifier");
    }
    return "`" + identifier + "`";
  }

  private Long nullSafeLong(Long value) {
    return value == null ? 0L : value;
  }
}
