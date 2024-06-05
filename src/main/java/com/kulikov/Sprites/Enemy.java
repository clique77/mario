package com.kulikov.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.kulikov.Screens.PlayScreen;

public abstract class Enemy extends Sprite {
  protected World world;
  protected PlayScreen screen;
  public Vector2 velocity;

  public Body b2body;

  /**
   * Конструктор класу Enemy.
   *
   * @param screen Екран гри.
   * @param x      Початкова позиція по осі X.
   * @param y      Початкова позиція по осі Y.
   */
  public Enemy(PlayScreen screen, float x, float y) {
    this.world = screen.getWorld();
    this.screen = screen;
    setPosition(x, y);
    defineEnemy();
    velocity = new Vector2(1, 0);
    b2body.setActive(false);
  }

  /**
   * Визначення ворога.
   */
  protected abstract void defineEnemy();

  /**
   * Метод викликається, коли герой наносить удар зверху.
   */
  public abstract void hitOnHead();

  /**
   * Зміна напрямку руху ворога.
   *
   * @param x true, якщо потрібно змінити напрямок по осі X.
   * @param y true, якщо потрібно змінити напрямок по осі Y.
   */
  public void reverseVelocity(boolean x, boolean y) {
    if (x) {
      velocity.x = -velocity.x;
    }
    if (y) {
      velocity.y = -velocity.y;
    }
  }

  /**
   * Оновлення стану ворога.
   *
   * @param delta Проміжок часу.
   */
  public abstract void update(float delta);

}
