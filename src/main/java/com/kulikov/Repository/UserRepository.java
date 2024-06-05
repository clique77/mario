package com.kulikov.Repository;

import com.kulikov.Entity.User;
import com.kulikov.Mapper.UserMapper;
import com.kulikov.connection.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * The UserRepository class provides methods for accessing and manipulating user data in the database.
 * It extends the GenericRepository class and implements specific functionality for user-related operations.
 *
 * @author [Author Name]
 * @version 1.0
 * @since 2024-05-16
 */
@Repository
public class UserRepository extends GenericRepository<User> {

  /**
   * Constructs a new UserRepository with the specified row mapper and connection manager.
   *
   * @param rowMapper        The mapper used to map database rows to User objects.
   * @param connectionManager The manager for obtaining database connections.
   */
  public UserRepository(UserMapper rowMapper, ConnectionManager connectionManager) {
    super(rowMapper, "users", connectionManager);
  }

  /**
   * Retrieves a user by their username from the database.
   *
   * @param username The username of the user to find.
   * @return The User object if found, otherwise null.
   */
  public User findByUsername(String username) {
    // SQL query to retrieve user by username
    String sql = "SELECT * FROM users WHERE username = ?";
    try (Connection connection = connectionManager.get();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, username);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return rowMapper.mapRow(resultSet);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace(); // Handle or log exception
    }
    return null;
  }

  /**
   * Checks if a user with the specified username exists in the database.
   *
   * @param username The username to check for existence.
   * @return True if a user with the username exists, otherwise false.
   */
  public boolean existsByUsername(String username) {
    // SQL query to check existence of username
    String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
    try (Connection connection = connectionManager.get();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, username);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getInt(1) > 0;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace(); // Handle or log exception
    }
    return false;
  }

  /**
   * Checks if a user with the specified email exists in the database.
   *
   * @param email The email to check for existence.
   * @return True if a user with the email exists, otherwise false.
   */
  public boolean existsByEmail(String email) {
    // SQL query to check existence of email
    String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
    try (Connection connection = connectionManager.get();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, email);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getInt(1) > 0;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace(); // Handle or log exception
    }
    return false;
  }

  /**
   * Returns an empty string as there are no additional SQL arguments required for User repository.
   *
   * @return An empty string.
   */
  @Override
  protected String additionalSqlArguments() {
    return "";
  }

  /**
   * Returns the list of attributes for the user table.
   *
   * @return The list of attributes for the user table.
   */
  @Override
  protected List<String> tableAttributes() {
    return Arrays.asList("user_id", "password", "username", "email");
  }

  /**
   * Returns the list of values for the given user entity.
   *
   * @param entity The user entity.
   * @return The list of values for the given user entity.
   */
  @Override
  protected List<Object> tableValues(User entity) {
    return Arrays.asList(entity.getId(), entity.getPassword(), entity.getUsername(), entity.getEmail());
  }
}
