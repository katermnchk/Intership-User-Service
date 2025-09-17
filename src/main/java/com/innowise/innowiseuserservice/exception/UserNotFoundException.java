package com.innowise.innowiseuserservice.exception;

import java.util.List;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(Long id) {
    super("User with ID " + id.toString() + " not found");
  }

  public UserNotFoundException(String email) {
    super("User with email: " + email + " not found");
  }

  public UserNotFoundException(List<Long> ids) {
    super("Users with IDs: " + ids.toString() + " not found");
  }
}
