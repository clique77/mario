package com.kulikov.exception;

/**
 * Виняток, що виникає при помилці обробки електронної пошти.
 */
public class EmailException extends RuntimeException {

  /**
   * Створює новий виняток із вказаним повідомленням.
   *
   * @param message Повідомлення про помилку
   */
  public EmailException(String message) {
    super(message);
  }
}

