package com.healthunspoken.insightsapi.dto;

import java.time.LocalDateTime;

public record CommentRow(
    String databaseName,
    String commentId,
    String videoId,
    String videoTitle,
    String channelId,
    String channelTitle,
    String authorDisplayName,
    String text,
    String detectedLanguage,
    Long likeCount,
    String publishedAt,
    LocalDateTime fetchedAtUtc,
    String fetchedAtIst) {}
