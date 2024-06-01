package main.com.kulikov.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.com.kulikov.MarioBros;

public class GameOverScreen implements Screen {
  private Viewport viewport;
  private Stage stage;
  private Game game;
  private SpriteBatch batch;
  private String levelFilename;

  private Texture backgroundTexture;
  private TextureRegion backgroundRegion;

  public GameOverScreen(Game game, SpriteBatch batch, String levelFilename) {
    this.batch = batch;
    this.game = game;
    this.levelFilename = levelFilename;
    viewport = new StretchViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
    stage = new Stage(viewport, ((MarioBros) game).getBatch());

    backgroundTexture = new Texture("sad_mario.jpg");
    backgroundRegion = new TextureRegion(backgroundTexture);
    Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

    Table table = new Table();
    table.center();
    table.setFillParent(true);

    Label gameOverLabel = new Label("Game Over", font);
    Label playAgain = new Label("Click To Play Again", font);
    table.add(gameOverLabel).expandX();
    table.row();
    table.add(playAgain).expandX().padTop(10f);

    stage.addActor(table);

    MarioBros.getAssetManager().get("audio/music/mario_music.ogg", Music.class).pause();
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {
    if (Gdx.input.justTouched()) {
      game.setScreen(new PlayScreen(game, batch, levelFilename));
      dispose();
    }
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();
    batch.draw(backgroundRegion, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
    batch.end();

    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height);
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void hide() {

  }

  @Override
  public void dispose() {
    stage.dispose();
    backgroundTexture.dispose();
  }
}
