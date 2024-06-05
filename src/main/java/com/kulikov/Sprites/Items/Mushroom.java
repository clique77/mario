package com.kulikov.Sprites.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.kulikov.MarioBros;
import com.kulikov.Sprites.Mario;
import com.kulikov.Screens.PlayScreen;

public class Mushroom extends Item {

  public Mushroom(PlayScreen screen, float x, float y) {
    super(screen, x, y);
    setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
    velocity = new Vector2(0.5f,0);
  }

  @Override
  public void defineItem() {
    BodyDef bdef = new BodyDef();
    bdef.position.set(getX(), getY());
    bdef.type = BodyDef.BodyType.DynamicBody;
    body = world.createBody(bdef);

    FixtureDef fdef = new FixtureDef();
    CircleShape shape = new CircleShape();
    shape.setRadius(8 / MarioBros.PPM);
    fdef.filter.categoryBits = MarioBros.ITEM_BIT;
    fdef.filter.maskBits = MarioBros.MARIO_BIT | MarioBros.OBJECT_BIT | MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT;

    fdef.shape = shape;
    body.createFixture(fdef).setUserData(this);
    shape.dispose();
  }

  @Override
  public void use(Mario mario) {
    if (!mario.isBig()) {
      mario.grow();
    }
    destroy();
  }

  @Override
  public void update(float delta) {
    super.update(delta);

    setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    velocity.y = body.getLinearVelocity().y;
    body.setLinearVelocity(velocity);
  }
}
