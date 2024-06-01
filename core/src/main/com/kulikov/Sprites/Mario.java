package main.com.kulikov.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import lombok.Getter;
import main.com.kulikov.MarioBros;
import main.com.kulikov.Screens.PlayScreen;

public class Mario extends Sprite {
  public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD}
  public State currentState;
  public State previousState;
  public World world;
  public Body b2body;
  private TextureRegion marioStand;
  private Animation<TextureRegion> marioRun;
  private TextureRegion marioJump;
  private TextureRegion bigMarioStand;
  private TextureRegion bigMarioJump;
  private TextureRegion marioDead;
  private Animation<TextureRegion> bigMarioRun;
  private Animation<TextureRegion> growMario;

  private boolean runningRight;
  @Getter
  private float stateTimer;
  private boolean marioIsBig;
  private boolean runGrowAnimation;

  private boolean timeToDefineBigMario;
  private boolean timeToRedefineMario;
  private boolean marioIsDead;

  // Track if Mario is on the ground
  @Getter
  private boolean onGround;

  public Mario(PlayScreen screen){
    this.world = screen.getWorld();
    currentState = State.STANDING;
    previousState = State.STANDING;
    stateTimer = 0;
    runningRight = true;
    onGround = true;

    Array<TextureRegion> frames = new Array<>();
    for (int i = 1; i < 4; i++) {
      frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
    }
    marioRun = new Animation(0.1f, frames);
    frames.clear();

    for (int i = 1; i < 4; i++) {
      frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
    }
    bigMarioRun = new Animation(0.1f, frames);
    frames.clear();

    frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
    frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
    frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
    frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
    growMario = new Animation(0.2f, frames);

    marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

    marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
    bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);
    frames.clear();

    defineMario();
    marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
    bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

    setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
    setRegion(marioStand);
  }

  public void update(float delta){
    if(marioIsBig){
      setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / MarioBros.PPM);
    }
    else{
      setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
    }
    setRegion(getFrame(delta));

    if(timeToDefineBigMario){
      defineBigMario();
    }
    if(timeToRedefineMario) {
      redefineMario();
    }

    // Update onGround status based on velocity
    onGround = b2body.getLinearVelocity().y == 0;
  }

  public TextureRegion getFrame(float delta){
    currentState = getState();

    TextureRegion region;
    switch(currentState) {
      case DEAD:
        region = marioDead;
        break;
      case GROWING:
        region = growMario.getKeyFrame(stateTimer);
        if(growMario.isAnimationFinished(stateTimer)){
          runGrowAnimation = false;
        }
        break;
      case JUMPING:
        region = marioIsBig ? bigMarioJump : marioJump;
        break;
      case RUNNING:
        region = marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
        break;
      case FALLING:
      case STANDING:
      default:
        region = marioIsBig ? bigMarioStand : marioStand;
        break;
    }

    if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
      region.flip(true, false);
      runningRight = false;
    } else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
      region.flip(true, false);
      runningRight = true;
    }

    stateTimer = currentState == previousState ? stateTimer + delta : 0;
    previousState = currentState;
    return region;
  }

  public State getState(){
    if(marioIsDead){
      return State.DEAD;
    }
    else if(runGrowAnimation){
      return State.GROWING;
    }
    else if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)){
      return State.JUMPING;
    }
    else if(b2body.getLinearVelocity().y < 0){
      return State.FALLING;
    }
    else if(b2body.getLinearVelocity().x != 0){
      return State.RUNNING;
    }
    else{
      return State.STANDING;
    }
  }

  public void grow(){
    runGrowAnimation = true;
    marioIsBig = true;
    timeToDefineBigMario = true;
    setBounds(getX(), getY(), getWidth(), getHeight() * 2);
    MarioBros.getAssetManager().get("audio/sounds/powerup.wav", Sound.class).play();
  }

  public void redefineMario(){
    Vector2 position = b2body.getPosition();
    world.destroyBody(b2body);

    BodyDef bdef = new BodyDef();
    bdef.position.set(position);
    bdef.type = BodyDef.BodyType.DynamicBody;
    b2body = world.createBody(bdef);

    FixtureDef fdef = new FixtureDef();
    CircleShape shape = new CircleShape();
    shape.setRadius(6 / MarioBros.PPM);
    fdef.filter.categoryBits = MarioBros.MARIO_BIT;
    fdef.filter.maskBits = MarioBros.GROUND_BIT |
        MarioBros.COIN_BIT |
        MarioBros.BRICK_BIT |
        MarioBros.ENEMY_BIT |
        MarioBros.OBJECT_BIT |
        MarioBros.ENEMY_HEAD_BIT |
        MarioBros.ITEM_BIT;

    fdef.shape = shape;
    b2body.createFixture(fdef).setUserData(this);

    EdgeShape head = new EdgeShape();
    head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
    fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
    fdef.shape = head;
    fdef.isSensor = true;

    b2body.createFixture(fdef).setUserData(this);

    timeToRedefineMario = false;
    b2body.setLinearDamping(2f);
  }

  public void defineBigMario(){
    Vector2 currentPosition = b2body.getPosition();
    world.destroyBody(b2body);

    BodyDef bdef = new BodyDef();
    bdef.position.set(currentPosition.add(0, 10 / MarioBros.PPM));
    bdef.type = BodyDef.BodyType.DynamicBody;
    b2body = world.createBody(bdef);

    FixtureDef fdef = new FixtureDef();
    CircleShape shape = new CircleShape();
    shape.setRadius(6 / MarioBros.PPM);
    fdef.filter.categoryBits = MarioBros.MARIO_BIT;
    fdef.filter.maskBits = MarioBros.GROUND_BIT |
        MarioBros.COIN_BIT |
        MarioBros.BRICK_BIT |
        MarioBros.ENEMY_BIT |
        MarioBros.OBJECT_BIT |
        MarioBros.ENEMY_HEAD_BIT |
        MarioBros.ITEM_BIT;

    fdef.shape = shape;
    b2body.createFixture(fdef).setUserData(this);
    shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
    b2body.createFixture(fdef).setUserData(this);

    EdgeShape head = new EdgeShape();
    head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
    fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
    fdef.shape = head;
    fdef.isSensor = true;

    b2body.createFixture(fdef).setUserData(this);
    timeToDefineBigMario = false;
    b2body.setLinearDamping(2f);
  }

  public void defineMario(){
    BodyDef bdef = new BodyDef();
    bdef.position.set(200 / MarioBros.PPM, 32 / MarioBros.PPM);
    bdef.type = BodyDef.BodyType.DynamicBody;
    b2body = world.createBody(bdef);

    FixtureDef fdef = new FixtureDef();
    CircleShape shape = new CircleShape();
    shape.setRadius(6 / MarioBros.PPM);
    fdef.filter.categoryBits = MarioBros.MARIO_BIT;
    fdef.filter.maskBits = MarioBros.GROUND_BIT |
        MarioBros.COIN_BIT |
        MarioBros.BRICK_BIT |
        MarioBros.ENEMY_BIT |
        MarioBros.OBJECT_BIT |
        MarioBros.ENEMY_HEAD_BIT |
        MarioBros.ITEM_BIT;

    fdef.shape = shape;
    b2body.createFixture(fdef).setUserData(this);

    EdgeShape head = new EdgeShape();
    head.set(new Vector2(-2 / MarioBros.PPM, 6 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 6 / MarioBros.PPM));
    fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
    fdef.shape = head;
    fdef.isSensor = true;

    b2body.createFixture(fdef).setUserData(this);

    b2body.setLinearDamping(2f);
  }

  public boolean isBig(){
    return marioIsBig;
  }

  public void hit(){
    if(marioIsBig){
      marioIsBig = false;
      timeToRedefineMario = true;
      setBounds(getX(), getY(), getWidth(), getHeight() / 2);
      MarioBros.getAssetManager().get("audio/sounds/powerdown.wav", Sound.class).play();
    }
    else if(!marioIsBig){
      MarioBros.getAssetManager().get("audio/music/mario_music.ogg", Music.class).play();
      MarioBros.getAssetManager().get("audio/sounds/mariodie.wav", Sound.class).play();
      marioIsDead = true;
      Filter filter = new Filter();
      filter.maskBits = MarioBros.NOTHING_BIT;
      for(Fixture fixture : b2body.getFixtureList()){
        fixture.setFilterData(filter);
      }
      b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
    }
  }

  public boolean isDead(){
    return marioIsDead;
  }

  public void jump() {
    if (isOnGround()) {
      b2body.applyLinearImpulse(new Vector2(0, 3f), b2body.getWorldCenter(), true);
      onGround = false;
    }
  }

}
