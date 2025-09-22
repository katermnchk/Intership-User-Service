package com.innowise.innowiseuserservice.exception;

public class CardNotFoundException extends RuntimeException {

  public CardNotFoundException(Long id) {
    super("Card with ID " + id.toString() + " not found");
  }
}
