package com.healthunspoken.insightsapi.dto;

import java.time.LocalDate;
import java.util.List;

public record CommentsResponse(
    LocalDate dateIst,
    CommentDatabase database,
    int limit,
    int count,
    List<CommentRow> items) {}
