package com.kulikov.Repository.excpetion;

public class LastRecordNotFoundException extends RuntimeException{
  public LastRecordNotFoundException(String message) {
    super(message);
  }
}
