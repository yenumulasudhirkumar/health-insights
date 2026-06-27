package com.healthunspoken.insightsapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SelectedCommentsRequest(
    String selectedBy,
    String intendedUse,
    String qualityLabel,
    String reviewNote,
    @NotEmpty List<@Valid SelectedCommentItem> comments) {

  public record SelectedCommentItem(
      String databaseName,
      String database,
      @NotBlank String commentId,
      String videoId,
      String videoTitle,
      String channelId,
      String channelTitle,
      String authorDisplayName,
      @NotBlank String text,
      Long likeCount,
      String publishedAt,
      String fetchedAtIst,
      String detectedLanguage,
      String intendedUse,
      String qualityLabel,
      String reviewNote) {}
}
