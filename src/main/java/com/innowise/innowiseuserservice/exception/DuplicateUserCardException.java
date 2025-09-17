package com.innowise.innowiseuserservice.exception;

public class DuplicateUserCardException extends RuntimeException {

  public DuplicateUserCardException(String number) {
    super("This user already has card with number " + number);
  }
}
