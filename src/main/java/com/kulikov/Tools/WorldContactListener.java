package com.kulikov.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.kulikov.MarioBros;
import com.kulikov.Sprites.Mario;
import com.kulikov.Sprites.Enemy;
import com.kulikov.Sprites.InteractiveTileObject;
import com.kulikov.Sprites.Items.Item;

/**
 * Обробник зіткнень у світі Box2D для гри MarioBros.
 */
public class WorldContactListener implements ContactListener {

  /**
   * Обробка початку зіткнення.
   *
   * @param contact Зіткнення
   */
  @Override
  public void beginContact(Contact contact) {
    Fixture fixA = contact.getFixtureA();
    Fixture fixB = contact.getFixtureB();

    int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

    switch (cDef) {
      case MarioBros.MARIO_HEAD_BIT | MarioBros.BRICK_BIT:
      case MarioBros.MARIO_HEAD_BIT | MarioBros.COIN_BIT:
        if (fixA.getFilterData().categoryBits == MarioBros.MARIO_HEAD_BIT) {
          ((InteractiveTileObject) fixB.getUserData()).onHeadHit(((Mario) fixA.getUserData()));
        } else {
          ((InteractiveTileObject) fixA.getUserData()).onHeadHit(((Mario) fixB.getUserData()));
        }
        break;
      case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:
        if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT) {
          ((Enemy) fixA.getUserData()).hitOnHead();
        } else {
          ((Enemy) fixB.getUserData()).hitOnHead();
        }
        break;
      case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT:
      case MarioBros.ENEMY_BIT | MarioBros.BRICK_BIT:
        if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT) {
          ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
        } else {
          ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
        }
        break;
      case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:
        if (fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT) {
          ((Mario) fixA.getUserData()).hit();
        } else {
          ((Mario) fixB.getUserData()).hit();
        }
        break;
      // If two objects are Enemy
      case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT:
        ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
        ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
        break;
      case MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT:
        if (fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT) {
          ((Item) fixA.getUserData()).reverseVelocity(true, false);
        } else {
          ((Item) fixB.getUserData()).reverseVelocity(true, false);
        }
        break;
      case MarioBros.ITEM_BIT | MarioBros.MARIO_BIT:
        if (fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT) {
          ((Item) fixA.getUserData()).use((Mario) fixB.getUserData());
        } else {
          ((Item) fixB.getUserData()).use((Mario) fixA.getUserData());
        }
        break;
    }
  }

  /**
   * Обробка завершення зіткнення.
   *
   * @param contact Зіткнення
   */
  @Override
  public void endContact(Contact contact) {
  }

  /**
   * Обробка перед розрахунком зіткнень.
   *
   * @param contact  Зіткнення
   * @param manifold Поверхня зіткнення
   */
  @Override
  public void preSolve(Contact contact, Manifold manifold) {
  }

  /**
   * Обробка після розрахунку зіткнень.
   *
   * @param contact          Зіткнення
   * @param contactImpulse   Імпульс зіткнення
   */
  @Override
  public void postSolve(Contact contact, ContactImpulse contactImpulse) {
  }
}
