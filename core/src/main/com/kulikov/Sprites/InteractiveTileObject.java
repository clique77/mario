package main.com.kulikov.Sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import main.com.kulikov.MarioBros;
import main.com.kulikov.Screens.PlayScreen;

public abstract class InteractiveTileObject {
  protected World world;
  protected TiledMap map;
  protected TiledMapTile tile;
  protected Rectangle bounds;
  protected Body body;
  protected Fixture fixture;
  protected PlayScreen screen;
  protected MapObject object;

  /**
   * Конструктор класу InteractiveTileObject.
   *
   * @param screen Екран гри.
   * @param object Об'єкт карти.
   */
  public InteractiveTileObject(PlayScreen screen, MapObject object) {
    this.object = object;
    this.screen = screen;
    this.world = screen.getWorld();
    this.map = screen.getMap();
    this.bounds = ((RectangleMapObject) object).getRectangle();

    BodyDef bdef = new BodyDef();
    FixtureDef fdef = new FixtureDef();
    PolygonShape shape = new PolygonShape();

    bdef.type = BodyDef.BodyType.StaticBody;
    bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / MarioBros.PPM, (bounds.getY() + bounds.getHeight() / 2) / MarioBros.PPM);

    body = world.createBody(bdef);

    shape.setAsBox(bounds.getWidth() / 2 / MarioBros.PPM, bounds.getHeight() / 2 / MarioBros.PPM);
    fdef.shape = shape;
    fixture = body.createFixture(fdef);
  }

  /**
   * Обробка удару по голові.
   *
   * @param mario Головний герой.
   */
  public abstract void onHeadHit(Mario mario);

  /**
   * Встановлення категорії фільтра.
   *
   * @param filterBit Категорія фільтра.
   */
  public void setCategoryFilter(short filterBit) {
    Filter filter = new Filter();
    filter.categoryBits = filterBit;
    fixture.setFilterData(filter);
  }

  /**
   * Отримання комірки з тайлового шару.
   *
   * @return Комірка тайлового шару.
   */
  public TiledMapTileLayer.Cell getCell() {
    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
    return layer.getCell((int) (body.getPosition().x * MarioBros.PPM / 16), (int) (body.getPosition().y * MarioBros.PPM / 16));
  }
}
