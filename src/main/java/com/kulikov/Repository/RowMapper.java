package com.kulikov.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.stereotype.Component;

/**
 * The RowMapper interface defines a method for mapping ResultSet rows to entity objects.
 * Classes that implement this interface are responsible for mapping specific entity types.
 * This interface is annotated with Spring's @Component annotation to indicate that it is a component.
 *
 * @param <T> The type of entity to map ResultSet rows to.
 * @author [Author Name]
 * @version 1.0
 * @since 2024-05-16
 */
@Component
public interface RowMapper<T> {

  /**
   * Maps a ResultSet row to an entity object of type T.
   *
   * @param rs The ResultSet containing the row data.
   * @return An object of type T mapped from the ResultSet row.
   * @throws SQLException if a SQL exception occurs.
   */
  T mapRow(ResultSet rs) throws SQLException;
}
