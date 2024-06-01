package main.com.kulikov.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.com.kulikov.MarioBros;
import main.com.kulikov.Scenes.Hud;
import main.com.kulikov.Sprites.Items.Item;
import main.com.kulikov.Sprites.Items.ItemDefinition;
import main.com.kulikov.Sprites.Items.Mushroom;
import main.com.kulikov.Sprites.Mario;
import main.com.kulikov.Tools.B2WorldCreator;
import main.com.kulikov.Tools.WorldContactListener;
import main.com.kulikov.Sprites.Enemy;
import java.util.concurrent.LinkedBlockingQueue;


public class PlayScreen extends InputAdapter implements Screen {
  private Game game;
  private TextureAtlas atlas;
  private SpriteBatch batch;
  private OrthographicCamera gamecamera;
  private Viewport gamePort;
  private Hud hud;
  private TmxMapLoader mapLoader;
  private TiledMap tiledMap;
  private OrthogonalTiledMapRenderer renderer;
  private World world;
  private Box2DDebugRenderer debugRenderer;
  private Mario player;
  private Music music;
  private B2WorldCreator creator;
  private Array<Item> items;
  public LinkedBlockingQueue<ItemDefinition> itemsToSpawn;
  private float menuOpenX = 3640 / MarioBros.PPM;
  private String levelFilename;
  private Preferences prefs;

  private Stage stage;
  private Skin skin;
  private TextButton menuButton;

  public PlayScreen(Game game, SpriteBatch batch, String levelFile) {
    this.levelFilename = levelFile;

    prefs = Gdx.app.getPreferences("MarioBrosPrefs");

    prefs.putString("currentLevel", levelFile);
    prefs.flush();
    atlas = new TextureAtlas(Gdx.files.internal("Mario_and_Enemies.pack"));

    this.game = game;
    this.batch = batch;

    gamecamera = new OrthographicCamera();
    gamePort = new StretchViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gamecamera);

    mapLoader = new TmxMapLoader();
    tiledMap = mapLoader.load(levelFile);
    renderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / MarioBros.PPM);
    gamecamera.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

    world = new World(new Vector2(0, -3), true);
    player = new Mario(this);
    debugRenderer = new Box2DDebugRenderer();

    creator = new B2WorldCreator(this);

    hud = new Hud(batch, game, levelFile);

    world.setContactListener(new WorldContactListener());

    music = MarioBros.getAssetManager().get("audio/music/mario_music.ogg", Music.class);
    music.setLooping(true);
    music.play();

    items = new Array<Item>();
    itemsToSpawn = new LinkedBlockingQueue<ItemDefinition>();

    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);
  }

  public void spawnItem(ItemDefinition itemDefinition) {
    itemsToSpawn.add(itemDefinition);
  }

  public void handleSpawningItems() {
    if (!itemsToSpawn.isEmpty()) {
      ItemDefinition itemDefinition = itemsToSpawn.poll();
      if (itemDefinition.type == Mushroom.class) {
        items.add(new Mushroom(this, itemDefinition.position.x, itemDefinition.position.y));
      }
    }
  }

  public TextureAtlas getAtlas() {
    return atlas;
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(new InputMultiplexer(this, stage));
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (button == Buttons.RIGHT) {
      game.setScreen(new MainMenuScreen(game, batch));
      dispose();
      return true;
    }
    return false;
  }

  public void handleInput(float delta) {
    if (player.currentState != Mario.State.DEAD) {
      if (Gdx.input.isKeyJustPressed(Keys.W)) {
        player.jump();
      }
      if (Gdx.input.isKeyPressed(Keys.D) && player.b2body.getLinearVelocity().x <= 2) {
        player.b2body.applyLinearImpulse(new Vector2(0.04f, 0), player.b2body.getWorldCenter(), true);
      }
      if (Gdx.input.isKeyPressed(Keys.A) && player.b2body.getLinearVelocity().x >= -2) {
        player.b2body.applyLinearImpulse(new Vector2(-0.04f, 0), player.b2body.getWorldCenter(), true);
      }
    }
  }

  public void update(float delta) {
    handleInput(delta);
    handleSpawningItems();

    world.step(1 / 60f, 6, 2);

    player.update(delta);
    for (Enemy enemy : creator.getGoombas()) {
      enemy.update(delta);
      if (enemy.getX() < player.getX() + 224 / MarioBros.PPM) {
        enemy.b2body.setActive(true);
      }
    }

    for (Item item : items) {
      item.update(delta);
    }

    hud.update(delta);
    float stopCameraX = 200 / MarioBros.PPM;
    if (player.b2body.getPosition().x > stopCameraX) {
      gamecamera.position.x = player.b2body.getPosition().x;
    }

    gamecamera.update();
    renderer.setView(gamecamera);

    if (player.b2body.getPosition().x >= menuOpenX) {
      game.setScreen(new LevelCompleteScreen(game, batch));
    }

  }

  @Override
  public void render(float delta) {
    update(delta);

    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    renderer.setView(gamecamera);
    renderer.render();

    batch.setProjectionMatrix(gamecamera.combined);
    batch.begin();
    player.draw(batch);
    for (Enemy enemy : creator.getGoombas()) {
      enemy.draw(batch);
    }
    for (Item item : items) {
      item.draw(batch);
    }
    batch.end();

    batch.setProjectionMatrix(hud.stage.getCamera().combined);
    hud.stage.draw();

    stage.act(delta);
    stage.draw();

    if (gameOver()) {
      game.setScreen(new GameOverScreen(game, batch, levelFilename));
      dispose();
    }
  }

  @Override
  public void resize(int width, int height) {
    gamePort.update(width, height);
  }

  public TiledMap getMap() {
    return tiledMap;
  }

  public World getWorld() {
    return world;
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
    tiledMap.dispose();
    renderer.dispose();
    world.dispose();
    debugRenderer.dispose();
    hud.dispose();
    stage.dispose();
  }

  public boolean gameOver() {
    if (player.currentState == Mario.State.DEAD && player.getStateTimer() > 3) {
      return true;
    } else {
      return false;
    }
  }
}