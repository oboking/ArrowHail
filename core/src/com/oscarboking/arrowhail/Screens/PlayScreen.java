package com.oscarboking.arrowhail.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oscarboking.arrowhail.AdHandler;
import com.oscarboking.arrowhail.GdxGame;
import com.oscarboking.arrowhail.Scenes.Hud;
import com.oscarboking.arrowhail.Sprites.Obstacle;
import com.oscarboking.arrowhail.Sprites.PowerUp;
import com.oscarboking.arrowhail.TouchInputProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sun.rmi.runtime.Log;

public class PlayScreen implements Screen {

    private GdxGame game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;
    private int worldTime = 0;

    //Box-2d variables
    private World world;
    private Box2DDebugRenderer b2dr;

    //Sprites
    private List<Obstacle> obstacles= new ArrayList<Obstacle>();
    private List<PowerUp> powerUps= new ArrayList<PowerUp>();

    //background stuff
    Texture background1, background2;
    float yMax, yCoordBg1, yCoordBg2;
    int BACKGROUND_MOVE_SPEED = 200; // pixels per second. Put your value here.

    //game-sesson stuff
    private Boolean hasStarted = false;
    private Boolean canPause = false;
    private Boolean isPaused = false;
    private Boolean hasDied = false;
    private Boolean hasShield = false;

    private int spawnFrequency = 50;
    private float fallSpeed = 6f;
    private Boolean isSlowMo = false;
    private float lastFallSpeed;

    private TouchInputProcessor inputProcessor;
    InputMultiplexer inputMultiplexer;

    private Random random;

    //prefs for stats

    Preferences prefs;
    private int arrowsDodgedThisGame=0;
    private int powerUpsThisGame=0;


    private AdHandler adHandler;

    public PlayScreen(final GdxGame game,AdHandler adHandler) {
        this.game = game;
        this.adHandler=adHandler;
        prefs = Gdx.app.getPreferences("prefs");

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(GdxGame.V_WIDTH,GdxGame.V_HEIGHT,gameCam);
        hud = new Hud(game,adHandler);

        gameCam.position.set(gamePort.getWorldWidth()/2,gamePort.getWorldHeight()/2, 0);

        world = new World(new Vector2(0,0), true);
        b2dr = new Box2DDebugRenderer();

        //background stuff
        background1 = new Texture(Gdx.files.internal("hexa_background.png"));
        background2 = new Texture(Gdx.files.internal("hexa_background.png")); // identical
        yMax = 957;
        yCoordBg1 = yMax; yCoordBg2 = 0;

        InputProcessor hudInputProcessor = hud.stage;
        InputProcessor gameInputProcessor = new InputProcessor(){
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if(!isPaused) {
                    if (!hasStarted) { //start first game of session
                        hasStarted = true;
                        hud.setHasStarted(true);
                    } else if (hasStarted) { //play another game after dying
                        hasDied = false;
                        hud.hideGameOverText();
                        obstacles.clear();
                        powerUps.clear();
                        canPause=false;
                    }
                }else{
                    isPaused=false;
                    hud.showPause(false);
                    BACKGROUND_MOVE_SPEED = 200;
                    for(Obstacle obstacle : obstacles){
                        obstacle.setFallSpeed(lastFallSpeed);

                    }
                    for(PowerUp powerUp : powerUps){
                        powerUp.setFallSpeed(6f);

                    }
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if(!canPause){
                    gameOver();
                }else{
                    isPaused=true;
                    canPause=false;
                    hud.showPauseText(true);
                    BACKGROUND_MOVE_SPEED = 0;
                    lastFallSpeed = fallSpeed;
                    for(Obstacle obstacle : obstacles){
                        obstacle.setFallSpeed(0);

                    }
                    for(PowerUp powerUp : powerUps){
                        powerUp.setFallSpeed(0);

                    }
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {

                for(int i = 0; i < obstacles.size(); i++) {
                    if(obstacles.get(i).isHit()){
                        if(hasShield){
                            hasShield=false;
                            hud.showShield(false);
                            obstacles.get(i).destroy();
                            obstacles.remove(i);
                        }else {
                            gameOver();
                        }
                    }

                }
                for(PowerUp powerUp : powerUps){

                }
                for(int j = 0; j<powerUps.size();j++){
                    PowerUp powerUp = powerUps.get(j);
                    if(powerUp.isHit()){
                        if(!powerUp.getHasBeenHandled()) {
                            powerUp.setHasBeenHandled(true);
                            handlePowerUpHit(powerUp.getType());
                            powerUps.get(j).destroy();
                            powerUps.remove(j);
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                return false;
            }
        };
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hudInputProcessor);
        inputMultiplexer.addProcessor(gameInputProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);

    }

    public void gameOver(){
        if(!hasDied) {
            hasDied = true;
            canPause = false;
            hud.showGameOverText(arrowsDodgedThisGame, powerUpsThisGame);
            spawnFrequency = 50;
            fallSpeed = 6f;
            arrowsDodgedThisGame = 0;
            powerUpsThisGame = 0;
        }

    }

    public void handlePowerUpHit(String type){
        powerUpsThisGame++;
        if(type.equals("x2")){
            hud.setScorePerTick(2);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            hud.setScorePerTick(1);
                            //resetting score per tick
                        }
                    },
                    8000
            );
        }else if(type.equals("slow-mo")){
            if(!isSlowMo) {
                fallSpeed = fallSpeed / 1.5f;
                isSlowMo = true;
                hud.showSlowMo(true);

                //set speed on existing arrows
                for (Obstacle obstacle : obstacles) {
                    obstacle.setFallSpeed(obstacle.getFallSpeed() / 1.5f);
                }
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                // your code here
                                isSlowMo = false;
                                fallSpeed = fallSpeed * 1.5f;
                                hud.showSlowMo(false);

                                //reset speed on existing arrows
                                for (Obstacle obstacle : obstacles) {
                                    obstacle.setFallSpeed(obstacle.getFallSpeed() * 1.5f);
                                }
                            }
                        },
                        8000
                );
            }
        }else if(type.equals("shield")){
            hasShield=true;
            hud.showShield(true);
        }else if(type.equals("pause")){
            hud.showPause(true);
            canPause=true;
        }
    }

    public void update(float dt){
        if(!isPaused) {
            worldTime++;
            world.step(1 / 60f, 6, 2);

            spawnObstacles();
            spawnPowerUps();

            hud.update(dt);
            gameCam.update();
        }
    }

    public void spawnObstacles(){
        double newSpawnFrequency;
        if(isSlowMo){
            newSpawnFrequency=this.spawnFrequency*1.5f;
        }else {
            newSpawnFrequency=this.spawnFrequency;
        }
        if(worldTime%newSpawnFrequency==0){
                Obstacle newObstacle = new Obstacle(world, gameCam, fallSpeed);
                obstacles.add(newObstacle);
        }
    }

    public void spawnPowerUps(){
        if(worldTime%200==0){//200
            random = new Random();
            if(random.nextInt(2)==0) {
                PowerUp newPowerUp = new PowerUp(world, gameCam);
                powerUps.add(newPowerUp);
            }
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(!hasDied) {
            update(delta);

            //move background
            yCoordBg1 -= BACKGROUND_MOVE_SPEED * Gdx.graphics.getDeltaTime();
            yCoordBg2 = yCoordBg1 - yMax;  // We move the background, not the camera
            if (yCoordBg1 < 0) {
                yCoordBg1 = yMax; yCoordBg2 = 0;
            }
        }

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        //background
        game.batch.draw(background1, 0, yCoordBg1);
        game.batch.draw(background2, 0, yCoordBg2);

        //move obstacles
        if(hasStarted) {
            for (Obstacle obstacle : obstacles) {
                if(obstacle.getSprite()!=null){
                    if (obstacle.getSprite().getY() < -600) {
                        obstacle.destroy();
                        obstacle = null;
                        arrowsDodgedThisGame++;
                        //obstacles.removeAll(Collections.singleton(null));
                        //obstacles.remove(obstacle);
                    } else {
                        obstacle.render(game.batch, delta);
                    }
                }

            }
            for (PowerUp powerup : powerUps){
                if(powerup.getSprite().getY()<-2000){
                    powerup = null;
                    //obstacles.removeAll(Collections.singleton(null));
                    //obstacles.remove(obstacle);
                }else{
                    powerup.render(game.batch, delta);
                }
            }
        }
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

        hud.stage.draw();

        if(hud.getScore()%50==0 && hud.getScore()>1){//for every 100th score, increase spawn frequency
            if(spawnFrequency>10) {
                spawnFrequency-=1;//more arrows spawned
                if(!isSlowMo) {
                    fallSpeed += 0.15;// fall faster
                }
            }
        }

        //b2dr.render(world,gameCam.combined); //debug lines


    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
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

    }
}
