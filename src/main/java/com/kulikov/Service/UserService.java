package com.kulikov.Service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.kulikov.Dto.UserDto;
import com.kulikov.Repository.UserRepository;
import com.kulikov.exception.ValidationException;
import com.kulikov.Entity.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * Сервіс для роботи з користувачами.
 */
@Service
public class UserService {
  private final UserRepository userRepository;
  private final Validator validator;

  /**
   * Конструктор класу UserService.
   *
   * @param userRepository Репозиторій користувачів
   * @param validator      Валідатор для перевірки об'єктів
   */
  public UserService(UserRepository userRepository, Validator validator) {
    this.userRepository = userRepository;
    this.validator = validator;
  }

  public void init() {
    System.out.println("UserService initialized.");
  }

  /**
   * Створює нового користувача на основі переданих даних.
   *
   * @param userStoreDto Дані користувача для створення
   * @return Створений користувач
   * @throws ValidationException якщо дані не відповідають вимогам валідації
   */
  public User create(UserDto userStoreDto) throws ValidationException {
    Set<ConstraintViolation<UserDto>> violations = validator.validate(userStoreDto);
    if (!violations.isEmpty()) {
      throw new ValidationException("Error creating user", violations);
    }

    String hashedPassword = BCrypt.withDefaults().hashToString(12, userStoreDto.getPassword().toCharArray());

    User user = User.builder()
        .username(userStoreDto.getUsername())
        .password(hashedPassword)
        .email(userStoreDto.getEmail()).build();

    return userRepository.save(user);
  }

  /**
   * Видаляє користувача з вказаним ідентифікатором, якщо він існує.
   *
   * @param userId Ідентифікатор користувача для видалення
   */
  public void deleteIfExists(int userId) {
    if (findById(userId) != null) {
      delete(userId);
      System.out.println("Користувач з id " + userId + " був успішно видалений.");
    } else {
      System.out.println("Користувача з id " + userId + " не знайдено.");
    }
  }

  /**
   * Оновлює інформацію про користувача.
   *
   * @param user Оновлені дані про користувача
   * @return true, якщо оновлення вдале, false - в іншому випадку
   */
  public boolean updateUser(User user) {
    return userRepository.update(user);
  }

  /**
   * Знаходить користувача за заданим ідентифікатором.
   *
   * @param userId Ідентифікатор користувача
   * @return Знайдений користувач або null, якщо користувач не знайдений
   */
  public User findById(int userId) {
    return userRepository.findById(userId);
  }

  /**
   * Зберігає дані про користувача, викликаючи метод create.
   *
   * @param userDto Дані користувача для збереження
   * @throws ValidationException якщо дані не відповідають вимогам валідації
   */
  public void saveUser(UserDto userDto) throws ValidationException {
    create(userDto);
  }

  private void delete(int userId) {
    userRepository.delete(userId);
  }
}
