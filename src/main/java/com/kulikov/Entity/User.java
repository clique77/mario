package com.kulikov.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Представляє сутність користувача.
 */
@Getter
@Builder
@AllArgsConstructor
@ToString
public class User implements Entity {

  /**
   * Ідентифікатор користувача.
   */
  @Setter
  private int id;

  /**
   * Пароль користувача.
   */
  @Setter
  private String password;

  /**
   * Ім'я користувача.
   */
  @Setter
  private String username;

  /**
   * Електронна пошта користувача.
   */
  @Setter
  private String email;
}