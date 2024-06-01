package main.com.kulikov.Tools;

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
import main.com.kulikov.Sprites.Brick;
import main.com.kulikov.Sprites.Coin;
import main.com.kulikov.MarioBros;
import main.com.kulikov.Screens.PlayScreen;
import main.com.kulikov.Sprites.Goomba;

public class B2WorldCreator {
  private Array<Goomba> goombas;

  public B2WorldCreator(PlayScreen screen) {
    World world = screen.getWorld();
    TiledMap map = screen.getMap();
    FixtureDef fixtureDef = new FixtureDef();

    BodyDef bodyDef = new BodyDef();
    PolygonShape shape = new PolygonShape();
    Body body;

    // Create ground
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

    // Create pipes
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

    // Create coins
    for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
      new Coin(screen, object);
    }

    // Create bricks
    for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
      new Brick(screen, object);
    }

    // Create goombas
    goombas = new Array<Goomba>();
    for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
      Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

      goombas.add(new Goomba(screen, rectangle.getX() / MarioBros.PPM, rectangle.getY() / MarioBros.PPM));
    }
  }

  public Array<Goomba> getGoombas() {
    return goombas;
  }
}