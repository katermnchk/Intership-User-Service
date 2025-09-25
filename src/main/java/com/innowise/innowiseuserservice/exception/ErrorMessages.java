package com.innowise.innowiseuserservice.exception;

public final class ErrorMessages {
  private ErrorMessages() {}

  public static final String NOT_FOUND = "Not found";
  public static final String CONFLICT = "Conflict";
  public static final String BAD_REQUEST = "Bad request";
  public static final String INTERNAL_SERVER_ERROR = "Internal server error";
  public static final String UNEXPECTED_ERROR = "An unexpected error occurred";
  public static final String MISSING_BODY = "Required request body is missing";
  public static final String INVALID_DATE_FORMAT = "Incorrect datetime format. Please, use yyyy-MM-dd";
  public static final String INVALID_PATH_VARIABLE = "Invalid path variable: ";
}
