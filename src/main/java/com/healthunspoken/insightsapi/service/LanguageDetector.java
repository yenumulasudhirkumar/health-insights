package com.healthunspoken.insightsapi.service;

import org.springframework.stereotype.Component;

@Component
public class LanguageDetector {

  public String detect(String text) {
    if (text == null || text.isBlank()) {
      return "UNKNOWN";
    }

    int latin = 0;
    int devanagari = 0;
    int telugu = 0;
    int letters = 0;

    for (int index = 0; index < text.length(); index++) {
      char value = text.charAt(index);
      if (!Character.isLetter(value)) {
        continue;
      }

      letters++;
      if (value >= '\u0900' && value <= '\u097F') {
        devanagari++;
      } else if (value >= '\u0C00' && value <= '\u0C7F') {
        telugu++;
      } else if ((value >= 'A' && value <= 'Z') || (value >= 'a' && value <= 'z')) {
        latin++;
      }
    }

    if (letters < 3) {
      return "UNKNOWN";
    }

    int scripts = 0;
    scripts += latin > 0 ? 1 : 0;
    scripts += devanagari > 0 ? 1 : 0;
    scripts += telugu > 0 ? 1 : 0;

    if (scripts > 1) {
      return "MIXED";
    }

    if (telugu > 0) {
      return "TELUGU";
    }
    if (devanagari > 0) {
      return "HINDI";
    }
    if (latin > 0) {
      return "ENGLISH";
    }

    return "UNKNOWN";
  }
}
