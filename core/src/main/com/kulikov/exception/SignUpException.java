package main.com.kulikov.exception;

/**
 * Виняток, що виникає при помилці реєстрації користувача.
 */
public class SignUpException extends RuntimeException {

  /**
   * Створює новий виняток із вказаним повідомленням.
   *
   * @param message Повідомлення про помилку
   */
  public SignUpException(String message) {
    super(message);
  }
}

