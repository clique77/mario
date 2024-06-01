package main.com.kulikov.Repository;

import main.com.kulikov.Entity.Entity;
import main.com.kulikov.Repository.excpetion.EntityNotFoundException;
import main.com.kulikov.Repository.excpetion.EntitySaveException;
import main.com.kulikov.Repository.excpetion.EntityUpdateException;
import main.com.kulikov.Repository.excpetion.LastRecordNotFoundException;
import main.com.kulikov.connection.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class GenericRepository<T extends Entity> implements Repository<T> {

  protected final RowMapper<T> rowMapper;
  protected final String tableName;
  protected final ConnectionManager connectionManager;

  protected GenericRepository(RowMapper<T> rowMapper, String tableName,
      ConnectionManager connectionManager) {
    this.rowMapper = rowMapper;
    this.tableName = tableName;
    this.connectionManager = connectionManager;
  }

  @Override
  public T save(T entity) {
    List<Object> values = tableValues(entity);
    List<String> attributes = tableAttributes();
    String attributesString = String.join(", ", attributes);
    String placeholders = Stream.generate(() -> "?")
        .limit(attributes.size())
        .collect(Collectors.joining(", "));
    final String sql =
        "INSERT INTO " + tableName + "(" + attributesString + ") " + "VALUES (" + placeholders
            + ")";
    try (Connection connection = connectionManager.get();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      for (int i = 0; i < values.size(); i++) {
        statement.setObject(i + 1, values.get(i));
      }
      statement.executeUpdate();
      entity.setId(getLastRecordId());
      return entity;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      throw new EntitySaveException(
          "Error adding to table");
    }
  }

  @Override
  public List<T> findAll() {
    final String sql = "SELECT * FROM " + tableName + " " + additionalSqlArguments();
    return findAllBy(sql);
  }

  @Override
  public T findById(int id) {
    final String sql = "SELECT * FROM " + tableName + " " + additionalSqlArguments() + "WHERE "
        + tableAttributes().get(0) + " = " + id;
    return findBy(sql);
  }

  @Override
  public T findByName(String name) {
    final String sql = "SELECT * FROM " + tableName + " " + additionalSqlArguments() + "WHERE "
        + tableAttributes().get(2) + " = " + name;
    return findBy(sql);
  }

  @Override
  public boolean update(T entity) {
    List<Object> values = tableValues(entity);
    List<String> attributes = tableAttributes();
    String attributesString =
        attributes.stream()
            .map(a -> a + " = ?")
            .collect(Collectors.joining(", "));
    final String sql =
        "UPDATE " + tableName + " SET " + attributesString + " WHERE " + tableAttributes().get(
            0) + " = " + entity.getId();
    try (Connection connection = connectionManager.get();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      for (int i = 0; i < values.size(); i++) {
        statement.setObject(i + 1, values.get(i));
      }
      return statement.execute();
    } catch (SQLException e) {
      throw new EntityUpdateException(
          "Помилка при оновленні існуючого запису в таблиці");
    }
  }

  @Override
  public boolean delete(int id) {
    final String sql =
        "DELETE FROM " + tableName + " WHERE " + tableAttributes().get(0) + "=" + id;
    try (Connection connection = connectionManager.get();
        Statement statement = connection.createStatement()) {
      return statement.execute(sql);
    } catch (SQLException e) {
      throw new EntityNotFoundException(
          "Помилка при видаленні запису з таблиці: " + tableName);
    }
  }

  protected T findBy(String sql) {
    try (Connection connection = connectionManager.get();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return rowMapper.mapRow(resultSet);
      } else {
        return null;
      }
    } catch (SQLException e) {
      throw new EntityNotFoundException(
          "Помилка при отриманні запису з таблиці: " + tableName + e.getMessage());
    }
  }

  protected List<T> findAllBy(String sql) {
    try (Connection connection = connectionManager.get();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      ResultSet resultSet = statement.executeQuery();
      List<T> entities = new ArrayList<>();
      while (resultSet.next()) {
        entities.add(rowMapper.mapRow(resultSet));
      }
      return entities;
    } catch (SQLException e) {
      throw new EntityNotFoundException(
          "Помилка при отриманні всіх записів з таблиці: " + tableName);
    }
  }

  protected int getLastRecordId() {
    final String sql = "SELECT * FROM " + tableName + " ORDER BY " + tableAttributes().get(0)
        + " DESC LIMIT 1";

    try (Statement statement = connectionManager.get().createStatement()) {
      ResultSet resultSet = statement.executeQuery(sql);
      if (resultSet.next()) {
        return resultSet.getInt(tableAttributes().get(0));
      }
    } catch (SQLException e) {
      throw new LastRecordNotFoundException(
          "Не вдалося отримати ідентифікатор останнього запису");
    }
    return 1;
  }


  protected abstract List<String> tableAttributes();

  protected abstract List<Object> tableValues(T entity);

  protected abstract String additionalSqlArguments();
}
