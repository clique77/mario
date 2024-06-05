package com.kulikov.exception;

/**
 * Виняток, що виникає при помилці аутентифікації користувача через невірний логін або пароль.
 */
public class AuthentificationException extends RuntimeException {

  /**
   * Конструктор за замовчуванням, який встановлює повідомлення про помилку.
   */
  public AuthentificationException() {
    super("Не вірний логін чи пароль.");
  }
}
