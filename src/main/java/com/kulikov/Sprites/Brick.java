package com.kulikov.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.kulikov.MarioBros;
import com.kulikov.Scenes.Hud;
import com.kulikov.Screens.PlayScreen;

public class Brick extends InteractiveTileObject {

  /**
   * Конструктор класу Brick.
   *
   * @param screen Екран гри.
   * @param object Об'єкт карти.
   */
  public Brick(PlayScreen screen, MapObject object) {
    super(screen, object);
    fixture.setUserData(this);
    setCategoryFilter(MarioBros.BRICK_BIT);
  }

  /**
   * Обробка відображення удару головою Маріо.
   *
   * @param mario Маріо, який вдарився об'єктом.
   */
  @Override
  public void onHeadHit(Mario mario) {
    if (mario.isBig()) {
      Gdx.app.log("Brick", "Collision");
      setCategoryFilter(MarioBros.DESTROYED_BIT);
      getCell().setTile(null);
      Hud.addScore(200);
      MarioBros.getAssetManager().get("audio/sounds/breakblock.wav", Sound.class).play();
    } else {
      MarioBros.getAssetManager().get("audio/sounds/bump.wav", Sound.class).play();
    }
  }
}
