package com.innowise.innowiseuserservice.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.innowise.innowiseuserservice.dto.responsies.MyErrorResponse;
import com.innowise.innowiseuserservice.exception.CardNotFoundException;
import com.innowise.innowiseuserservice.exception.DuplicateUserCardException;
import com.innowise.innowiseuserservice.exception.EmailAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import java.net.URISyntaxException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class MyExceptionHandler {

  @ExceptionHandler({
      CardNotFoundException.class,
      URISyntaxException.class
  })
  public ResponseEntity<MyErrorResponse> handleEntityNotFoundException(RuntimeException e) {
    log.warn("Entity not found: {}", e.getMessage(), e);
    return buildErrorResponse(
        HttpStatus.NOT_FOUND,
        List.of(e.getMessage()),
        "Not found");
  }

  @ExceptionHandler({
      EmailAlreadyExistsException.class,
      DuplicateUserCardException.class
  })
  public ResponseEntity<MyErrorResponse> handleEntityAlreadyExistsException(RuntimeException e) {
    log.warn("Entity already exists: {}", e.getMessage(), e);

    return buildErrorResponse(
        HttpStatus.CONFLICT,
        List.of(e.getMessage()),
        "Conflict"
    );
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<MyErrorResponse> handleInvalidFormatException(HttpMessageNotReadableException e) {
    Throwable cause = e.getCause();
    String errorMessage = "Bad request";

    if (cause instanceof InvalidFormatException) {
      errorMessage = "Incorrect datetime format. Please, use yyyy-MM-dd";
      log.info("Invalid format exception: {}", cause.getMessage());
    } else if (e.getMessage() != null && e.getMessage().contains("Required request body is missing")) {
      errorMessage = "Required request body is missing";
      log.info("Missing request body");
    } else {
      log.warn("HttpMessageNotReadableException: {}", e.getMessage(), e);
    }

    return buildErrorResponse(
        HttpStatus.BAD_REQUEST,
        List.of(errorMessage),
        "Bad request");
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<MyErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
    List<String> errors = ex.getConstraintViolations().stream()
        .map(fieldError -> fieldError.getPropertyPath() + ": " + fieldError.getMessage())
        .toList();

    log.info("Constraint violations: {} errors", errors.size());
    errors.forEach(error -> log.debug("Constraint violation: {}", error));

    return buildErrorResponse (
        HttpStatus.BAD_REQUEST,
        errors,
        "Bad request"
    );
  }


  private ResponseEntity<MyErrorResponse>
  buildErrorResponse(HttpStatus status, List<String> messages, String error) {
    MyErrorResponse errorResponse = new MyErrorResponse(status.value(), messages, error);
    return new ResponseEntity<>(errorResponse, status);
  }
}
