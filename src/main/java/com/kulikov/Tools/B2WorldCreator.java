package com.kulikov.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.kulikov.Sprites.Brick;
import com.kulikov.Sprites.Coin;
import com.kulikov.MarioBros;
import com.kulikov.Screens.PlayScreen;
import com.kulikov.Sprites.Goomba;

/**
 * Клас, що створює об'єкти у світі Box2D на основі об'єктів з карти Tiled.
 */
public class B2WorldCreator {
  private Array<Goomba> goombas;

  /**
   * Конструктор класу B2WorldCreator.
   *
   * @param screen екран гри, який використовує цей світ
   */
  public B2WorldCreator(PlayScreen screen) {
    World world = screen.getWorld();
    TiledMap map = screen.getMap();
    FixtureDef fixtureDef = new FixtureDef();

    BodyDef bodyDef = new BodyDef();
    PolygonShape shape = new PolygonShape();
    Body body;

    // Створення землі
    for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
      Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

      bodyDef.type = BodyDef.BodyType.StaticBody;
      bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioBros.PPM,
          (rectangle.getY() + rectangle.getHeight() / 2) / MarioBros.PPM);

      body = world.createBody(bodyDef);

      shape.setAsBox(rectangle.getWidth() / 2 / MarioBros.PPM, rectangle.getHeight() / 2 / MarioBros.PPM);
      fixtureDef.shape = shape;
      body.createFixture(fixtureDef);
    }

    // Створення труб
    for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
      Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

      bodyDef.type = BodyDef.BodyType.StaticBody;
      bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioBros.PPM,
          (rectangle.getY() + rectangle.getHeight() / 2) / MarioBros.PPM);

      body = world.createBody(bodyDef);

      shape.setAsBox(rectangle.getWidth() / 2 / MarioBros.PPM, rectangle.getHeight() / 2 / MarioBros.PPM);
      fixtureDef.shape = shape;
      fixtureDef.filter.categoryBits = MarioBros.OBJECT_BIT;
      body.createFixture(fixtureDef);
    }

    // Створення монет
    for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
      new Coin(screen, object);
    }

    // Створення цеглини
    for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
      new Brick(screen, object);
    }

    // Створення гумбаців
    goombas = new Array<Goomba>();
    for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
      Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

      goombas.add(new Goomba(screen, rectangle.getX() / MarioBros.PPM, rectangle.getY() / MarioBros.PPM));
    }
  }

  /**
   * Отримує масив гумбаців у світі.
   *
   * @return масив гумбаців
   */
  public Array<Goomba> getGoombas() {
    return goombas;
  }
}
