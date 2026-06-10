package com.healthunspoken.insightsapi.service;

import com.healthunspoken.insightsapi.dto.CommentDatabase;
import com.healthunspoken.insightsapi.dto.CommentsResponse;
import com.healthunspoken.insightsapi.repository.CommentQueryRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

  private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
  private final CommentQueryRepository repository;

  public CommentService(CommentQueryRepository repository) {
    this.repository = repository;
  }

  public CommentsResponse byDate(LocalDate date, CommentDatabase database, int limit) {
    int safeLimit = clampLimit(limit);
    var rows = repository.findByFetchedDate(date, database, safeLimit);
    return new CommentsResponse(date, database, safeLimit, rows.size(), rows);
  }

  public CommentsResponse yesterday(CommentDatabase database, int limit) {
    LocalDate yesterdayIst = LocalDate.now(IST).minusDays(1);
    return byDate(yesterdayIst, database, limit);
  }

  public CommentsResponse recent(LocalDate date, CommentDatabase database, int limit) {
    int safeLimit = clampLimit(limit);
    var rows = repository.findRecentByFetchedDate(date, database, safeLimit);
    return new CommentsResponse(date, database, safeLimit, rows.size(), rows);
  }

  public CommentsResponse relevantQuestions(LocalDate date, CommentDatabase database, int limit) {
    int safeLimit = clampLimit(limit);
    var rows = repository.findRelevantHealthQuestionsByFetchedDate(date, database, safeLimit);
    return new CommentsResponse(date, database, safeLimit, rows.size(), rows);
  }

  public CommentsResponse topQuestions(LocalDate date, CommentDatabase database, int limit) {
    int safeLimit = clampLimit(limit);
    var rows = repository.findTopQuestionCandidates(date, database, safeLimit);
    return new CommentsResponse(date, database, safeLimit, rows.size(), rows);
  }

  private int clampLimit(int limit) {
    if (limit <= 0) {
      return 100;
    }
    return Math.min(limit, 500);
  }
}
