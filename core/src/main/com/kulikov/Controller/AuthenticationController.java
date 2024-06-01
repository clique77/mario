package main.com.kulikov.Controller;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import main.com.kulikov.MarioBros;
import main.com.kulikov.Screens.MainMenuScreen;
import main.com.kulikov.Service.AuthenticationService;
import main.com.kulikov.Entity.User;
import main.com.kulikov.exception.AuthentificationException;

public class AuthenticationController extends ScreenAdapter {

  private final AuthenticationService authenticationService;
  private final Stage stage;
  private final TextField usernameTextField;
  private final TextField passwordTextField;
  private final Label errorLabel;
  private final SpriteBatch batch;
  private final MarioBros game;
  private final Table table;
  private Texture backgroundTexture;
  private Sprite backgroundSprite;

  public AuthenticationController(MarioBros game, AuthenticationService authenticationService, SpriteBatch batch) {
    this.game = game;
    this.authenticationService = authenticationService;
    this.batch = batch;
    this.stage = new Stage();
    this.table = new Table();

    backgroundTexture = new Texture("registratio.jpg");
    backgroundSprite = new Sprite(backgroundTexture);
    backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    BitmapFont font = new BitmapFont();

    TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
    textFieldStyle.font = font;
    textFieldStyle.fontColor = Color.WHITE;

    Label titleLabel = new Label("Log in", new Label.LabelStyle(font, Color.WHITE));
    titleLabel.setFontScale(3f);
    table.add(titleLabel).colspan(2).padBottom(10).center();
    table.row();

    final float fieldWidth = 400f;
    final float fieldHeight = 50f;
    final float padding = 20f;

    textFieldStyle.font.getData().setScale(2.5f);

    usernameTextField = new TextField("", textFieldStyle);
    usernameTextField.setAlignment(Align.left);

    passwordTextField = new TextField("", textFieldStyle);
    passwordTextField.setPasswordMode(true);
    passwordTextField.setPasswordCharacter('*');
    passwordTextField.setAlignment(Align.left);

    TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
    buttonStyle.font = font;

    TextButton.TextButtonStyle loginButtonStyle = new TextButton.TextButtonStyle();


    TextureRegionDrawable loginButtonBackground = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("button.png"))));
    loginButtonStyle.up = loginButtonBackground;

    BitmapFont buttonFont = new BitmapFont();

    loginButtonStyle.font = buttonFont;
    float fontScale = 3f;
    buttonFont.getData().setScale(fontScale);

    float buttonWidth = 550f;
    float buttonHeight = 200f;

    TextButton loginButton = new TextButton("Login", loginButtonStyle);

    TextButton registerButton = new TextButton("Register", buttonStyle);
    registerButton.getLabel().setFontScale(2f);

    registerButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        game.setScreen(new SignUpController(game, authenticationService, batch));
      }
    });

    table.setFillParent(true);
    table.defaults().pad(padding);

    final float labelFontSize = 2.5f;

    Label.LabelStyle usernameLabelStyle = new Label.LabelStyle(font, Color.WHITE);
    usernameLabelStyle.font.getData().setScale(labelFontSize);
    Label.LabelStyle passwordLabelStyle = new Label.LabelStyle(font, Color.WHITE);
    passwordLabelStyle.font.getData().setScale(labelFontSize);

    table.add(new Label("Username:", usernameLabelStyle)).right().padRight(5f);
    table.add(usernameTextField).width(fieldWidth).height(fieldHeight).left();
    table.row();
    table.add(new Label("Password:", passwordLabelStyle)).right().padRight(5f);
    table.add(passwordTextField).width(fieldWidth).height(fieldHeight).left();
    table.row();
    table.add(loginButton).colspan(2).width(buttonWidth).height(buttonHeight).padTop(padding).center();
    table.row();
    table.add(registerButton).colspan(2).width(200).height(60).padTop(10).center();
    table.row();

    errorLabel = new Label("", new Label.LabelStyle(font, Color.RED));
    table.add(errorLabel).colspan(2).padTop(10).center();

    stage.addActor(table);

    loginButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        authenticate();
      }
    });

    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();
    backgroundSprite.draw(batch);
    batch.end();

    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void dispose() {
    stage.dispose();
    backgroundTexture.dispose();
  }

  private void authenticate() {
    String username = usernameTextField.getText();
    String password = passwordTextField.getText();

    try {
      User authenticatedUser = authenticationService.authenticate(username, password);
      MainMenuScreen mainMenuScreen = new MainMenuScreen(game, batch);
      game.setScreen(mainMenuScreen);
    } catch (AuthentificationException e) {
      errorLabel.setText("Authentication failed: Invalid username or password");
    }
  }
}
