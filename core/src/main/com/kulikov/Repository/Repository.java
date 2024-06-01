package main.com.kulikov.Repository;

import java.util.List;

import java.util.List;

/**
 * A generic repository interface defining basic CRUD operations for entities.
 *
 * @param <T> The type of entity handled by this repository.
 */
public interface Repository<T> {

  /**
   * Saves the given entity.
   *
   * @param entity The entity to save.
   * @return The saved entity.
   */
  T save(T entity);

  /**
   * Retrieves all entities.
   *
   * @return A list of all entities.
   */
  List<T> findAll();

  /**
   * Retrieves an entity by its ID.
   *
   * @param id The ID of the entity to retrieve.
   * @return The entity with the specified ID, or null if not found.
   */
  T findById(int id);

  /**
   * Retrieves an entity by its name.
   *
   * @param name The name of the entity to retrieve.
   * @return The entity with the specified name, or null if not found.
   */
  T findByName(String name);

  /**
   * Updates the given entity.
   *
   * @param entity The entity to update.
   * @return true if the update was successful, false otherwise.
   */
  boolean update(T entity);

  /**
   * Deletes the entity with the specified ID.
   *
   * @param id The ID of the entity to delete.
   * @return true if the deletion was successful, false otherwise.
   */
  boolean delete(int id);
}
