package main.com.kulikov.Entity;


/**
 * Інтерфейс, який представляє сутність з ідентифікатором.
 */
public interface Entity {

  /**
   * Отримує ідентифікатор сутності.
   * @return Ідентифікатор сутності.
   */
  int getId();

  /**
   * Встановлює ідентифікатор сутності.
   * @param id Ідентифікатор сутності.
   */
  void setId(int id);
}
