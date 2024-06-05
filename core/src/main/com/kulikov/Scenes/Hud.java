package main.com.kulikov.Scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.com.kulikov.MarioBros;
import main.com.kulikov.Screens.GameOverScreen;

/**
 * Клас Hud відповідає за відображення інформації на екрані гравця під час гри.
 */
public class Hud implements Disposable {
  private Game game;
  public Stage stage;
  private Viewport viewport;
  private Integer worldTimer;
  private float timeCount;
  private static Integer score;
  private Label countdownLabel;
  private static Label scoreLabel;
  private Label timeLabel;
  private Label levelLabel;
  private Label worldLabel;
  private Label marioLabel;
  private SpriteBatch batch;
  private String levelFilename;

  /**
   * Конструктор класу Hud.
   *
   * @param batch         Спрайт бетч для відображення графіки.
   * @param game          Екземпляр гри.
   * @param levelFilename Шлях до файлу рівня.
   */
  public Hud(SpriteBatch batch, Game game, String levelFilename) {
    this.game = game;
    this.levelFilename = levelFilename;
    worldTimer = 100;
    timeCount = 0;
    score = 0;
    this.batch = batch;
    viewport = new FillViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT);
    stage = new Stage(viewport);

    Table table = new Table();
    table.top();
    table.setFillParent(true);

    BitmapFont font = new BitmapFont();
    Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

    countdownLabel = new Label(String.format("%03d", worldTimer), style);
    scoreLabel = new Label(String.format("%06d", score), style);
    timeLabel = new Label("TIME", style);
    marioLabel = new Label("MARIO", style);

    font.getData().setScale(0.5f);

    table.add(marioLabel).expandX().padTop(10);
    table.add(worldLabel).expandX().padTop(10);
    table.add(timeLabel).expandX().padTop(10);

    table.row();
    table.add(scoreLabel).expandX();
    table.add(levelLabel).expandX();
    table.add(countdownLabel).expandX();

    stage.addActor(table);
  }

  /**
   * Оновлює відображення інформації.
   *
   * @param delta Час з початку останнього кадру.
   */
  public void update(float delta) {
    timeCount += delta;
    if (timeCount >= 1) {
      worldTimer--;
      countdownLabel.setText(String.format("%03d", worldTimer));
      timeCount = 0;

      if (worldTimer <= 0) {
        game.setScreen(new GameOverScreen(game, batch, levelFilename));
      }
    }
  }

  /**
   * Додає очки гравцеві.
   *
   * @param value Кількість очків для додавання.
   */
  public static void addScore(int value) {
    score += value;
    scoreLabel.setText(String.format("%06d", score));
  }

  /**
   * Видалення екземпляра Hud та всіх пов'язаних ресурсів.
   */
  @Override
  public void dispose() {
    stage.dispose();
  }
}
