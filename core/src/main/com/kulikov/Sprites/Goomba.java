package main.com.kulikov.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import main.com.kulikov.MarioBros;
import main.com.kulikov.Screens.PlayScreen;

public class Goomba extends Enemy {

  private float stateTime;
  private Animation<TextureRegion> walkAnimation;
  private Array<TextureRegion> frames;
  private boolean setToDestroy;
  private boolean destroyed;

  /**
   * Конструктор класу Goomba.
   *
   * @param screen Екран гри.
   * @param x      Початкова позиція по осі X.
   * @param y      Початкова позиція по осі Y.
   */
  public Goomba(PlayScreen screen, float x, float y) {
    super(screen, x, y);
    frames = new Array<TextureRegion>();
    for (int i = 0; i < 2; i++) {
      frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
    }
    walkAnimation = new Animation<TextureRegion>(0.4f, frames);
    stateTime = 0;
    setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
    setToDestroy = false;
    destroyed = false;
    velocity = new Vector2(0.6f, 0);
  }

  /**
   * Оновлення стану Goomba.
   *
   * @param delta Проміжок часу.
   */
  public void update(float delta) {
    stateTime += delta;
    if (setToDestroy && !destroyed) {
      world.destroyBody(b2body);
      destroyed = true;
      setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
      stateTime = 0;
    } else if (!destroyed) {
      b2body.setLinearVelocity(velocity);
      setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
      setRegion(walkAnimation.getKeyFrame(stateTime, true));
    }
  }

  /**
   * Визначення Goomba в фізичному світі.
   */
  @Override
  protected void defineEnemy() {
    BodyDef bdef = new BodyDef();
    bdef.position.set(getX(), getY());
    bdef.type = BodyDef.BodyType.DynamicBody;
    b2body = world.createBody(bdef);

    FixtureDef fdef = new FixtureDef();
    CircleShape shape = new CircleShape();
    shape.setRadius(8 / MarioBros.PPM);

    fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
    fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT | MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT | MarioBros.MARIO_BIT;

    fdef.shape = shape;
    b2body.createFixture(fdef).setUserData(this);
    shape.dispose();

    PolygonShape head = new PolygonShape();
    Vector2[] vertice = new Vector2[4];
    vertice[0] = new Vector2(-5, 8).scl(1 / MarioBros.PPM);
    vertice[1] = new Vector2(5, 8).scl(1 / MarioBros.PPM);
    vertice[2] = new Vector2(-3, 3).scl(1 / MarioBros.PPM);
    vertice[3] = new Vector2(3, 3).scl(1 / MarioBros.PPM);
    head.set(vertice);

    fdef.shape = head;
    fdef.restitution = 1f;
    fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
    b2body.createFixture(fdef).setUserData(this);
  }

  /**
   * Малює Goomba.
   *
   * @param batch Batch для малювання.
   */
  public void draw(Batch batch) {
    if (!destroyed || stateTime < 1) {
      super.draw(batch);
    }
  }

  /**
   * Обробка удару зверху.
   */
  @Override
  public void hitOnHead() {
    setToDestroy = true;
    MarioBros.getAssetManager().get("audio/sounds/stomp.wav", Sound.class).play();
  }
}
