package com.healthunspoken.insightsapi.dto;

public enum CommentDatabase {
  MAIN,
  HEALTH,
  BOTH;

  public static CommentDatabase from(String value) {
    if (value == null || value.isBlank()) {
      return BOTH;
    }
    return CommentDatabase.valueOf(value.trim().toUpperCase());
  }
}
