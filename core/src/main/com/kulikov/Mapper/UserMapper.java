package main.com.kulikov.Mapper;

import main.com.kulikov.Entity.User;
import main.com.kulikov.Repository.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.stereotype.Component;

/**
 * The UserMapper class maps ResultSet rows to User entities.
 * It implements the RowMapper interface.
 * This class is annotated with Spring's @Component annotation to indicate that it is a component.
 *
 * @author [Author Name]
 * @version 1.0
 * @since 2024-05-16
 */
@Component
public class UserMapper implements RowMapper<User> {

  /**
   * Maps a ResultSet row to a User entity.
   *
   * @param rs The ResultSet containing the row data.
   * @return A User object mapped from the ResultSet row.
   * @throws SQLException if a SQL exception occurs.
   */
  @Override
  public User mapRow(ResultSet rs) throws SQLException {
    // Map ResultSet columns to User fields and build the User object
    return User.builder()
        .id(rs.getInt("user_id"))
        .password(rs.getString("password"))
        .username(rs.getString("username"))
        .email(rs.getString("email"))
        .build();
  }
}