package com.healthunspoken.insightsapi.controller;

import com.healthunspoken.insightsapi.dto.CommentDatabase;
import com.healthunspoken.insightsapi.dto.CommentsResponse;
import com.healthunspoken.insightsapi.service.CommentService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

  private final CommentService commentService;

  public CommentsController(CommentService commentService) {
    this.commentService = commentService;
  }

  @GetMapping("/yesterday")
  public CommentsResponse yesterday(
      @RequestParam(defaultValue = "both") String database,
      @RequestParam(defaultValue = "100") int limit) {
    return commentService.yesterday(CommentDatabase.from(database), limit);
  }

  @GetMapping("/by-date")
  public CommentsResponse byDate(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(defaultValue = "both") String database,
      @RequestParam(defaultValue = "100") int limit) {
    return commentService.byDate(date, CommentDatabase.from(database), limit);
  }

  @GetMapping("/top-questions")
  public CommentsResponse topQuestions(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(defaultValue = "both") String database,
      @RequestParam(defaultValue = "50") int limit) {
    return commentService.topQuestions(date, CommentDatabase.from(database), limit);
  }
}
