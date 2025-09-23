package com.innowise.innowiseuserservice.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.innowise.innowiseuserservice.dto.responsies.ApiErrorResponse;
import com.innowise.innowiseuserservice.exception.CardNotFoundException;
import com.innowise.innowiseuserservice.exception.DuplicateUserCardException;
import com.innowise.innowiseuserservice.exception.EmailAlreadyExistsException;
import com.innowise.innowiseuserservice.exception.ErrorMessages;
import com.innowise.innowiseuserservice.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.net.URISyntaxException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
      CardNotFoundException.class,
      UserNotFoundException.class,
      URISyntaxException.class
  })
  public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(RuntimeException e) {
    log.warn("Entity not found: {}", e.getMessage(), e);
    return buildErrorResponse(
        HttpStatus.NOT_FOUND,
        List.of(e.getMessage()),
        ErrorMessages.NOT_FOUND);
  }

  @ExceptionHandler({
      EmailAlreadyExistsException.class,
      DuplicateUserCardException.class
  })
  public ResponseEntity<ApiErrorResponse> handleEntityAlreadyExistsException(RuntimeException e) {
    log.warn("Entity already exists: {}", e.getMessage(), e);

    return buildErrorResponse(
        HttpStatus.CONFLICT,
        List.of(e.getMessage()),
        ErrorMessages.CONFLICT
    );
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidFormatException(HttpMessageNotReadableException e) {
    Throwable cause = e.getCause();
    String errorMessage = ErrorMessages.BAD_REQUEST;

    if (cause instanceof InvalidFormatException) {
      errorMessage = ErrorMessages.INVALID_DATE_FORMAT;
      log.info("Invalid format exception: {}", cause.getMessage());
    } else if (e.getMessage() != null && e.getMessage().contains("Required request body is missing")) {
      errorMessage = ErrorMessages.MISSING_BODY;
      log.info("Missing request body");
    } else {
      log.warn("HttpMessageNotReadableException: {}", e.getMessage(), e);
    }

    return buildErrorResponse(
        HttpStatus.BAD_REQUEST,
        List.of(errorMessage),
        ErrorMessages.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
    List<String> errors = e.getConstraintViolations().stream()
        .map(fieldError -> fieldError.getPropertyPath() + ": " + fieldError.getMessage())
        .toList();

    log.info("Constraint violations: {} errors", errors.size());
    errors.forEach(error -> log.debug("Constraint violation: {}", error));

    return buildErrorResponse (
        HttpStatus.BAD_REQUEST,
        errors,
        ErrorMessages.BAD_REQUEST
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
    List<String> errors = e.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .toList();

    log.info("Validation failed: {} errors", errors.size());
    errors.forEach(error -> log.debug("Validation error: {}", error));

    return buildErrorResponse (
        HttpStatus.BAD_REQUEST,
        errors,
        ErrorMessages.BAD_REQUEST
    );
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleAllExceptions(Exception e) {
    log.error("Unexpected error: {}", e.getMessage(), e);
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        List.of(ErrorMessages.UNEXPECTED_ERROR),
        ErrorMessages.UNEXPECTED_ERROR
    );
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
    log.info("Method argument type mismatch: parameter '{}', value '{}'", e.getName(), e.getValue());

    return buildErrorResponse(
        HttpStatus.BAD_REQUEST,
        List.of(ErrorMessages.INVALID_PATH_VARIABLE + e.getName()),
        ErrorMessages.INVALID_PATH_VARIABLE
    );
  }

  private ResponseEntity<ApiErrorResponse>
  buildErrorResponse(HttpStatus status, List<String> messages, String error) {
    ApiErrorResponse errorResponse = new ApiErrorResponse(status.value(), messages, error);
    return new ResponseEntity<>(errorResponse, status);
  }
}
