package main.com.kulikov.exception;

/**
 * Виняток, що виникає, коли спроба аутентифікації користувача вже відбулася.
 */
public class UserAlreadyAuthenticatedException extends RuntimeException {

  /**
   * Створює новий виняток із вказаним повідомленням.
   *
   * @param message Повідомлення про помилку
   */
  public UserAlreadyAuthenticatedException(String message) {
    super(message);
  }
}

