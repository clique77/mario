package main.com.kulikov.Service;

import main.com.kulikov.Repository.UserRepository;
import main.com.kulikov.Dto.UserDto;
import main.com.kulikov.Entity.User;
import org.springframework.stereotype.Service;

/**
 * Сервіс для реєстрації нових користувачів.
 */
@Service
public class SignUpService {
  private final UserService userService;
  private final UserRepository userRepository;

  /**
   * Конструктор класу SignUpService.
   *
   * @param userService    Сервіс користувачів
   * @param userRepository Репозиторій користувачів
   */
  public SignUpService(UserService userService, UserRepository userRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
  }

  /**
   * Реєструє нового користувача з заданими даними.
   *
   * @param username Ім'я користувача
   * @param password Пароль
   * @param email    Електронна пошта
   * @return Створений користувач
   */
  public User signUp(String username, String password, String email) {
    UserDto userStoreDto = new UserDto(username, password, email);
    return userService.create(userStoreDto);
  }

  /**
   * Перевіряє, чи існує користувач з заданим іменем користувача.
   *
   * @param username Ім'я користувача для перевірки
   * @return true, якщо користувач існує, false - в іншому випадку
   */
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  /**
   * Перевіряє, чи існує користувач з заданою електронною поштою.
   *
   * @param email Електронна пошта для перевірки
   * @return true, якщо користувач існує, false - в іншому випадку
   */
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}