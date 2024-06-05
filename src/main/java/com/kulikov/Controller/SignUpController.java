/**
 * Клас SignUpController керує процесом реєстрації в грі.
 * Він надає інтерфейс користувачам для реєстрації, надаючи ім'я користувача, електронну пошту та пароль.
 * Після успішної реєстрації перенаправляє користувача на екран аутентифікації.
 * Якщо реєстрація не вдається через помилки валідації або наявність даних користувача, відображаються відповідні повідомлення про помилки.
 */
package com.kulikov.Controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.kulikov.MarioBros;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import com.kulikov.Dto.UserDto;
import com.kulikov.Service.AuthenticationService;
import com.kulikov.Service.SignUpService;

import java.util.Set;

public class SignUpController extends ScreenAdapter {

  private final MarioBros game;
  private Stage stage;
  private BitmapFont font;
  private SpriteBatch batch;
  private SignUpService signUpService;
  private TextureRegion backgroundTexture;
  private String errorMessage;
  private boolean showError;
  private AuthenticationService authenticationService;

  /**
   * Конструктор об'єкта SignUpController.
   *
   * @param game                  Екземпляр гри.
   * @param authenticationService Служба автентифікації користувача.
   * @param batch                 Sprite batch для відображення.
   */
  public SignUpController(MarioBros game, AuthenticationService authenticationService, SpriteBatch batch) {
    this.game = game;
    this.authenticationService = authenticationService;
    this.batch = batch;
    this.stage = new Stage(new StretchViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera()));
    this.font = new BitmapFont();
    this.signUpService = MarioBros.springContext.getBean(SignUpService.class);
    this.backgroundTexture = new TextureRegion(new Texture(Gdx.files.internal("registratio.jpg")));
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);

    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    Label titleLabel = new Label("Register", new Label.LabelStyle(font, Color.WHITE));
    titleLabel.setFontScale(1f);
    table.add(titleLabel).colspan(2).padBottom(10).center();
    table.row();

    final float fontSize = 0.5f;
    final float fieldWidth = 120f;
    final float fieldHeight = 15f;
    final float padding = 1f;

    final TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle(font, Color.WHITE, null, null, null);
    textFieldStyle.font.getData().setScale(fontSize);


    final TextField usernameTextField = new TextField("", textFieldStyle);
    usernameTextField.getStyle().font.getData().setScale(fontSize);
    usernameTextField.setAlignment(Align.left);

    final TextField emailTextField = new TextField("", textFieldStyle);
    emailTextField.getStyle().font.getData().setScale(fontSize);
    emailTextField.setAlignment(Align.left);

    final TextField passwordTextField = new TextField("", textFieldStyle);
    passwordTextField.getStyle().font.getData().setScale(fontSize);
    passwordTextField.setAlignment(Align.left);

    table.add(new Label("Username: ", new Label.LabelStyle(font, Color.WHITE))).right().padRight(5f);
    table.add(usernameTextField).width(fieldWidth).height(fieldHeight).padBottom(padding).left();
    table.row();

    table.add(new Label("Email: ", new Label.LabelStyle(font, Color.WHITE))).right().padRight(5f);
    table.add(emailTextField).width(fieldWidth).height(fieldHeight).padBottom(padding).left();
    table.row();

    table.add(new Label("Password: ", new Label.LabelStyle(font, Color.WHITE))).right().padRight(5f);
    table.add(passwordTextField).width(fieldWidth).height(fieldHeight).padBottom(padding).left();
    table.row();

    TextButton.TextButtonStyle registerButtonStyle = new TextButton.TextButtonStyle();

    TextureRegionDrawable buttonBackground = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("button.png"))));
    registerButtonStyle.up = buttonBackground;

    BitmapFont buttonFont = new BitmapFont();

    registerButtonStyle.font = buttonFont;
    float fontScale = 0.2f;
    buttonFont.getData().setScale(fontScale);

    float buttonWidth = 150f;
    float buttonHeight = 50f;

    TextButton registerButton = new TextButton("Register", registerButtonStyle);
    registerButton.getLabel().setFontScale(0.7f);
    registerButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        signUp(usernameTextField.getText(), emailTextField.getText(), passwordTextField.getText());
      }
    });

    table.add(registerButton).colspan(2).width(buttonWidth).height(buttonHeight).padTop(padding).center();
    table.row();

    Label errorLabel = new Label("", new Label.LabelStyle(font, Color.RED));
    errorLabel.setFontScale(0.5f);
    errorLabel.setName("errorLabel");
    table.add(errorLabel).colspan(2).padTop(padding).center();
    table.row();

    TextButton backButton = new TextButton("Already have an account", new TextButton.TextButtonStyle(null, null, null, font));
    backButton.getLabel().setFontScale(0.5f);
    backButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        game.setScreen(new AuthenticationController(game, authenticationService, batch));
      }
    });

    table.add(backButton).colspan(2).width(200).height(30).
        padTop(padding).center();
  }

  /**
   * Спроба зареєструвати нового користувача з наданими обліковими даними.
   * Перевіряє валідність даних користувача та перевіряє наявність існуючих користувачів.
   * Відображає відповідні повідомлення про помилки, якщо виникають проблеми з валідацією або дублюванням користувачів.
   *
   * @param username Ім'я користувача нового користувача.
   * @param email    Електронна пошта нового користувача.
   * @param password Пароль нового користувача.
   */
  private void signUp(String username, String email, String password) {
    UserDto userDto = new UserDto(username, password, email);

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    if (!violations.isEmpty()) {
      StringBuilder errorMessage = new StringBuilder();
      for (ConstraintViolation<UserDto> violation : violations) {
        errorMessage.append(violation.getMessage()).append("\n");
      }
      showErrorAlert(errorMessage.toString());
      return;
    }

    if (signUpService.existsByUsername(username)) {
      showErrorAlert("Користувач з таким ім'ям вже існує.");
      return;
    }

    if (signUpService.existsByEmail(email)) {
      showErrorAlert("Користувач з такою електронною адресою вже існує.");
      return;
    }

    signUpService.signUp(username, password, email);
    game.setScreen(new AuthenticationController(game, authenticationService, batch));
  }

  /**
   * Відображення повідомлення про помилку.
   *
   * @param message Повідомлення про помилку, що відображається.
   */
  private void showErrorAlert(String message) {
    this.errorMessage = message;
    this.showError = true;
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);

    batch.begin();
    batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    batch.end();

    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    stage.draw();

    Label errorLabel = stage.getRoot().findActor("errorLabel");
    if (errorLabel != null) {
      if (showError && errorMessage != null) {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
      } else {
        errorLabel.setVisible(false);
      }
    }
  }

  @Override
  public void dispose() {
    stage.dispose();
    font.dispose();
    batch.dispose();
  }
}
