package com.healthunspoken.insightsapi.controller;

import com.healthunspoken.insightsapi.dto.CommentDatabase;
import com.healthunspoken.insightsapi.dto.CommentLanguage;
import com.healthunspoken.insightsapi.dto.CommentsResponse;
import com.healthunspoken.insightsapi.dto.SelectedCommentsRequest;
import com.healthunspoken.insightsapi.dto.SelectedCommentsResponse;
import com.healthunspoken.insightsapi.service.CommentService;
import com.healthunspoken.insightsapi.service.SelectedCommentService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

  private final CommentService commentService;
  private final SelectedCommentService selectedCommentService;

  public CommentsController(CommentService commentService, SelectedCommentService selectedCommentService) {
    this.commentService = commentService;
    this.selectedCommentService = selectedCommentService;
  }

  @GetMapping("/yesterday")
  public CommentsResponse yesterday(
      @RequestParam(defaultValue = "both") String database,
      @RequestParam(defaultValue = "all") String language,
      @RequestParam(defaultValue = "100") int limit) {
    return commentService.yesterday(CommentDatabase.from(database), CommentLanguage.from(language), limit);
  }

  @GetMapping("/by-date")
  public CommentsResponse byDate(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(defaultValue = "both") String database,
      @RequestParam(defaultValue = "all") String language,
      @RequestParam(defaultValue = "100") int limit) {
    return commentService.byDate(date, CommentDatabase.from(database), CommentLanguage.from(language), limit);
  }

  @GetMapping("/recent")
  public CommentsResponse recent(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(defaultValue = "both") String database,
      @RequestParam(defaultValue = "all") String language,
      @RequestParam(defaultValue = "100") int limit) {
    return commentService.recent(date, CommentDatabase.from(database), CommentLanguage.from(language), limit);
  }

  @GetMapping("/relevant-questions")
  public CommentsResponse relevantQuestions(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(defaultValue = "both") String database,
      @RequestParam(defaultValue = "all") String language,
      @RequestParam(defaultValue = "100") int limit) {
    return commentService.relevantQuestions(date, CommentDatabase.from(database), CommentLanguage.from(language), limit);
  }

  @GetMapping("/top-questions")
  public CommentsResponse topQuestions(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(defaultValue = "both") String database,
      @RequestParam(defaultValue = "all") String language,
      @RequestParam(defaultValue = "50") int limit) {
    return commentService.topQuestions(date, CommentDatabase.from(database), CommentLanguage.from(language), limit);
  }

  @PostMapping("/selected")
  public SelectedCommentsResponse saveSelected(@Valid @RequestBody SelectedCommentsRequest request) {
    return selectedCommentService.save(request);
  }
}
