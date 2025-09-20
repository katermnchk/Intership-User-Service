package com.innowise.innowiseuserservice.dto;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

@Getter
public class ApiResponse <T> {
  private final String message;
  private final String timestamp;
  private final int status;
  private final T data;

  public ApiResponse(int status, String message, T data) {
    this.status = status;
    this.message = message;
    this.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    this.data = data;
  }

}
