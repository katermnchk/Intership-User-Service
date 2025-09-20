package com.innowise.innowiseuserservice.dto.responsies;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Getter;

@Getter
public class MyErrorResponse {
  private final List<String> message;
  private final OffsetDateTime timestamp;
  private final int status;
  private final String error;

  public MyErrorResponse(int status, List<String> message, String error) {
    this.status = status;
    this.message = message;
    this.timestamp = OffsetDateTime.now(ZoneOffset.UTC);
    this.error = error;
  }

}
