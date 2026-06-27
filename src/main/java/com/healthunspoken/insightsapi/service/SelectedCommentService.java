package com.healthunspoken.insightsapi.service;

import com.healthunspoken.insightsapi.dto.SelectedCommentsRequest;
import com.healthunspoken.insightsapi.dto.SelectedCommentsResponse;
import com.healthunspoken.insightsapi.repository.SelectedCommentRepository;
import org.springframework.stereotype.Service;

@Service
public class SelectedCommentService {

  private final SelectedCommentRepository repository;
  private final LanguageDetector languageDetector;

  public SelectedCommentService(SelectedCommentRepository repository, LanguageDetector languageDetector) {
    this.repository = repository;
    this.languageDetector = languageDetector;
  }

  public SelectedCommentsResponse save(SelectedCommentsRequest request) {
    int saved = 0;
    String selectedBy =
        request.selectedBy() == null || request.selectedBy().isBlank() ? "sudhir" : request.selectedBy();

    for (SelectedCommentsRequest.SelectedCommentItem item : request.comments()) {
      String detectedLanguage =
          item.detectedLanguage() == null || item.detectedLanguage().isBlank()
              ? languageDetector.detect(item.text())
              : item.detectedLanguage();
      saved +=
          repository.save(
              item,
              selectedBy,
              request.intendedUse(),
              request.qualityLabel(),
              request.reviewNote(),
              detectedLanguage);
    }

    return new SelectedCommentsResponse(request.comments().size(), saved);
  }
}
