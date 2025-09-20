package com.innowise.innowiseuserservice.dto;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Getter;

@Getter
public class MyErrorResponse {
  final private List<String> message;
  final private String timestamp;
  final private int status;
  final private String error;

  public MyErrorResponse(int status, List<String> message, String error) {
    this.status = status;
    this.message = message;
    this.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    this.error = error;
  }

}
