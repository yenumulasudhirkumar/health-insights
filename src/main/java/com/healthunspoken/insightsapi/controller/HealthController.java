package com.healthunspoken.insightsapi.controller;

import java.time.ZonedDateTime;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping("/health")
  public Map<String, Object> health() {
    return Map.of(
        "status", "ok",
        "service", "health-insights-api",
        "time_ist", ZonedDateTime.now(java.time.ZoneId.of("Asia/Kolkata")).toString());
  }
}
