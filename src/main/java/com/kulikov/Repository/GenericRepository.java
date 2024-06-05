/**
 * Загальний репозиторій, який надає базові операції збереження, видалення, оновлення та пошуку для сутностей.
 * Використовується для роботи з базою даних за допомогою JDBC.
 *
 * @param <T> Тип сутності, яку представляє даний репозиторій.
 */
package com.kulikov.Repository;

import com.kulikov.Entity.Entity;
import com.kulikov.Repository.excpetion.EntityNotFoundException;
import com.kulikov.Repository.excpetion.EntitySaveException;
import com.kulikov.Repository.excpetion.EntityUpdateException;
import com.kulikov.Repository.excpetion.LastRecordNotFoundException;
import com.kulikov.connection.ConnectionManager;

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

  /**
   * Конструктор для створення екземпляра GenericRepository.
   *
   * @param rowMapper         Об'єкт, який відповідає за відображення рядків бази даних на сутність.
   * @param tableName         Назва таблиці в базі даних.
   * @param connectionManager Об'єкт, що забезпечує доступ до підключення до бази даних.
   */
  protected GenericRepository(RowMapper<T> rowMapper, String tableName,
      ConnectionManager connectionManager) {
    this.rowMapper = rowMapper;
    this.tableName = tableName;
    this.connectionManager = connectionManager;
  }

  /**
   * Зберігає сутність в базу даних.
   *
   * @param entity Сутність для збереження.
   * @return Сутність після збереження.
   * @throws EntitySaveException Виняток, що виникає при неможливості зберегти сутність.
   */
  @Override
  public T save(T entity) throws EntitySaveException {
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
          "Помилка при додаванні до таблиці");
    }
  }

  /**
   * Знаходить всі сутності у базі даних.
   *
   * @return Список всіх сутностей.
   * @throws EntityNotFoundException Виняток, що виникає при неможливості знайти сутності.
   */
  @Override
  public List<T> findAll() throws EntityNotFoundException {
    final String sql = "SELECT * FROM " + tableName + " " + additionalSqlArguments();
    return findAllBy(sql);
  }

  /**
   * Знаходить сутність за її ідентифікатором.
   *
   * @param id Ідентифікатор сутності.
   * @return Сутність з вказаним ідентифікатором або null, якщо сутність не знайдено.
   * @throws EntityNotFoundException Виняток, що виникає при неможливості знайти сутність за ідентифікатором.
   */
  @Override
  public T findById(int id) throws EntityNotFoundException {
    final String sql = "SELECT * FROM " + tableName + " " + additionalSqlArguments() + "WHERE "
        + tableAttributes().get(0) + " = " + id;
    return findBy(sql);
  }

  /**
   * Знаходить сутність за її ім'ям.
   *
   * @param name Ім'я сутності.
   * @return Сутність з вказаним ім'ям або null, якщо сутність не знайдено.
   * @throws EntityNotFoundException Виняток, що виникає при неможливості знайти сутність за ім'ям.
   */
  @Override
  public T findByName(String name) throws EntityNotFoundException {
    final String sql = "SELECT * FROM " + tableName + " "  + additionalSqlArguments() + "WHERE "
        + tableAttributes().get(2) + " = " + name;
    return findBy(sql);
  }

  /**
   * Оновлює існуючу сутність в базі даних.
   *
   * @param entity Сутність для оновлення.
   * @return true, якщо оновлення пройшло успішно, false - у протилежному випадку.
   * @throws EntityUpdateException Виняток, що виникає при неможливості оновити сутність.
   */
  @Override
  public boolean update(T entity) throws EntityUpdateException {
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

  /**
   * Видаляє сутність з бази даних за її ідентифікатором.
   *
   * @param id Ідентифікатор сутності, яку потрібно видалити.
   * @return true, якщо видалення пройшло успішно, false - у протилежному випадку.
   * @throws EntityNotFoundException Виняток, що виникає при неможливості знайти сутність для видалення.
   */
  @Override
  public boolean delete(int id) throws EntityNotFoundException {
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

  /**
   * Знаходить сутність за запитом SQL.
   *
   * @param sql SQL-запит для пошуку сутності.
   * @return Сутність, знайдена за вказаним SQL-запитом, або null, якщо сутність не знайдено.
   * @throws EntityNotFoundException Виняток, що виникає при неможливості знайти сутність за SQL-запитом.
   */
  protected T findBy(String sql) throws EntityNotFoundException {
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

  /**
   * Знаходить всі сутності за запитом SQL.
   *
   * @param sql SQL-запит для пошуку всіх сутностей.
   * @return Список всіх сутностей, знайдених за вказаним SQL-запитом.
   * @throws EntityNotFoundException Виняток, що виникає при неможливості знайти всі сутності за SQL-запитом.
   */
  protected List<T> findAllBy(String sql) throws EntityNotFoundException {
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

  /**
   * Отримує ідентифікатор останнього запису в таблиці.
   *
   * @return Ідентифікатор останнього запису в таблиці.
   * @throws LastRecordNotFoundException Виняток, що виникає при неможливості отримати ідентифікатор останнього запису.
   */
  protected int getLastRecordId() throws LastRecordNotFoundException {
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

  /**
   * Абстрактний метод для отримання списку атрибутів таблиці.
   *
   * @return Список атрибутів таблиці.
   */
  protected abstract List<String> tableAttributes();

  /**
   * Абстрактний метод для отримання значень таблиці для вказаної сутності.
   *
   * @param entity Сутність, для якої потрібно отримати значення.
   * @return Список значень таблиці.
   */
  protected abstract List<Object> tableValues(T entity);

/**
 * Абстрактний метод для отриман
 * Абстрактний метод для отримання додаткових аргументів SQL-запиту.
 *
 * @return Додаткові аргументи SQL-запиту.
 */
protected abstract String additionalSqlArguments();
}

