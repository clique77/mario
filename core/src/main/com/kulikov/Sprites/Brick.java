package main.com.kulikov.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import main.com.kulikov.MarioBros;
import main.com.kulikov.Scenes.Hud;
import main.com.kulikov.Screens.PlayScreen;

public class Brick extends InteractiveTileObject {
  public Brick(PlayScreen screen, MapObject object) {
    super(screen, object);
    fixture.setUserData(this);
    setCategoryFilter(MarioBros.BRICK_BIT);
  }

  @Override
  public void onHeadHit(Mario mario) {
    if(mario.isBig()){
      Gdx.app.log("Brick", "Collision");
      setCategoryFilter(MarioBros.DESTROYED_BIT);
      getCell().setTile(null);
      Hud.addScore(200);
      MarioBros.getAssetManager().get("audio/sounds/breakblock.wav", Sound.class).play();
    }
    else{
      MarioBros.getAssetManager().get("audio/sounds/bump.wav", Sound.class).play();
    }
  }
}
