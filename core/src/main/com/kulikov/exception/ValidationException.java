package main.com.kulikov.exception;

import jakarta.validation.ConstraintViolation;
import java.util.Set;

/**
 * Виняток, що виникає при помилці валідації об'єкта.
 */
public class ValidationException extends RuntimeException {

  private static final long serialVersionUID = 3145152535586258949L;

  private final Set<? extends ConstraintViolation<?>> violations;

  /**
   * Створює новий виняток із вказаним повідомленням та набором помилок валідації.
   *
   * @param message Повідомлення про помилку
   * @param violations Набір помилок валідації
   */
  public ValidationException(String message, Set<? extends ConstraintViolation<?>> violations) {
    super(message);
    this.violations = violations;
  }

  /**
   * Повертає набір помилок валідації.
   *
   * @return Набір помилок валідації
   */
  public Set<? extends ConstraintViolation<?>> getViolations() {
    return violations;
  }

  /**
   * Створює новий об'єкт {@code ValidationException} з додатковим суфіксом та набором помилок валідації.
   *
   * @param suffix Суфікс повідомлення про помилку
   * @param violations Набір помилок валідації
   * @return Новий об'єкт {@code ValidationException}
   */
  public static ValidationException create(String suffix, Set<? extends ConstraintViolation<?>> violations) {
    return new ValidationException("Помилка валідації при " + suffix, violations);
  }
}

