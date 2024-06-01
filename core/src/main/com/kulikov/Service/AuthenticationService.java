package main.com.kulikov.Service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.Getter;
import main.com.kulikov.Entity.User;
import main.com.kulikov.Repository.UserRepository;
import main.com.kulikov.exception.AuthentificationException;
import main.com.kulikov.exception.UserAlreadyAuthenticatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Сервіс для автентифікації користувачів.
 */
@Service
public class AuthenticationService {

  private final UserRepository userRepository;

  @Autowired
  public AuthenticationService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Getter
  private User user;

  /**
   * Аутентифікує користувача за логіном та паролем.
   *
   * @param login    Логін користувача
   * @param password Пароль користувача
   * @return Аутентифікований користувач
   * @throws AuthentificationException           якщо аутентифікація не вдалася
   * @throws UserAlreadyAuthenticatedException  якщо користувач вже авторизований
   */
  public User authenticate(String login, String password) {
    if (user != null) {
      throw new UserAlreadyAuthenticatedException(
          "Error: You already authenticated as " + user.getUsername());
    }

    User foundUser = userRepository.findByUsername(login);

    if (foundUser == null) {
      throw new AuthentificationException();
    }

    BCrypt.Result result = BCrypt.verifyer()
        .verify(password.toCharArray(), foundUser.getPassword().toCharArray());

    if (!result.verified) {
      throw new AuthentificationException();
    }

    user = foundUser;
    return user;
  }
}