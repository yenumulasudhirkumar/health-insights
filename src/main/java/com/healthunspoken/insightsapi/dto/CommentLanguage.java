package com.healthunspoken.insightsapi.dto;

import java.util.Locale;

public enum CommentLanguage {
  ALL,
  ENGLISH,
  HINDI,
  TELUGU,
  MIXED,
  UNKNOWN;

  public static CommentLanguage from(String value) {
    if (value == null || value.isBlank()) {
      return ALL;
    }

    return switch (value.trim().toLowerCase(Locale.ROOT)) {
      case "all" -> ALL;
      case "english", "en" -> ENGLISH;
      case "hindi", "hi" -> HINDI;
      case "telugu", "te" -> TELUGU;
      case "mixed", "mixed_indic", "mixed-indic" -> MIXED;
      case "unknown" -> UNKNOWN;
      default -> throw new IllegalArgumentException("Unsupported language: " + value);
    };
  }

  public boolean matches(String detectedLanguage) {
    if (this == ALL) {
      return true;
    }
    return name().equalsIgnoreCase(detectedLanguage);
  }
}
