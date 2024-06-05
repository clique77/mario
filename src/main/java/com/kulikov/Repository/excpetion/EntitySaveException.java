package com.kulikov.Repository.excpetion;

public class EntitySaveException extends RuntimeException {
  public EntitySaveException(String message) {
    super(message);
  }
}

