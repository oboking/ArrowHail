package com.oscarboking.arrowhail.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oscarboking.arrowhail.AdHandler;
import com.oscarboking.arrowhail.Screens.MenuScreen;

import java.util.Timer;
import java.util.TimerTask;

public class Hud {
    public Stage stage;
    public Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;
    private Integer highScore;

    private BitmapFont bitmapFont;
    Label scoreLabel;
    Label holdToStartLabel;
    Label highScoreLabel;
    Label holdToTryAgainLabel;
    Label finalScoreLabel;
    Label finalScoreTextLabel;
    private TextButton menuButton;

    private Image x2;
    private Image shield;
    private Image slowMo;
    private Image pause;

    private TextureAtlas atlas;
    protected Skin skin;
    private FreeTypeFontGenerator generator;
    private BitmapFont font;
    public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private Label.LabelStyle labelStyle;
    private TextButton.TextButtonStyle buttonStyle;

    private FreeTypeFontGenerator generatorBigFont;
    private BitmapFont bigFont;
    private Label.LabelStyle labelStyleBig;

    private FreeTypeFontGenerator generatorSmall;
    private BitmapFont smallFont;
    private Label.LabelStyle labelStyleSmall;

    private Boolean hasStarted = false;

    private com.oscarboking.arrowhail.GdxGame game;

    private Table table;

    private Integer scorePerTick=1;
    private Integer slowMoLeft=-1;
    private Label slowMoTime;
    private Boolean countingDownSlowMo = false;
    private Label pausedLabel;

    private Timer timer;

    private Preferences prefs;
    private AdHandler adHandler;


    public Hud(final com.oscarboking.arrowhail.GdxGame game, final AdHandler adHandler){
        this.game = game;
        this.adHandler=adHandler;
        worldTimer = 300;
        timeCount = 0;
        score = 0;
        viewport = new FitViewport(com.oscarboking.arrowhail.GdxGame.V_WIDTH, com.oscarboking.arrowhail.GdxGame.V_HEIGHT,new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        prefs = Gdx.app.getPreferences("prefs");
        highScore=prefs.getInteger("highScore",0);

        Gdx.input.setInputProcessor(stage);

        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;

        font=generator.generateFont(parameter);
        generator.dispose();

        generatorBigFont = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterBigFont = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterBigFont.size = 54;

        bigFont=generatorBigFont.generateFont(parameterBigFont);
        generatorBigFont.dispose();
        labelStyleBig = new Label.LabelStyle(bigFont,Color.WHITE);

        generatorSmall = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterSmallFont = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterSmallFont.size = 18;

        smallFont=generatorSmall.generateFont(parameterSmallFont);
        generatorSmall.dispose();
        labelStyleSmall = new Label.LabelStyle(smallFont,Color.WHITE);

        skin = new Skin();
        atlas = new TextureAtlas("button.pack");
        skin.addRegions(atlas);
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.getDrawable("button_up");
        buttonStyle.over = skin.getDrawable("button_down");
        buttonStyle.down = skin.getDrawable("button_down");
        buttonStyle.font = font;

        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        scoreLabel = new Label(score.toString(), labelStyle);
        highScoreLabel = new Label("High score: " + highScore, labelStyleSmall);
        holdToStartLabel = new Label("Hold to start", labelStyle);
        holdToTryAgainLabel = new Label("Hold to try again", labelStyle);
        finalScoreTextLabel = new Label("Final Score",labelStyle);
        finalScoreLabel = new Label(score.toString(),labelStyleBig);
        slowMoTime = new Label(slowMoLeft+"",labelStyleSmall);
        pausedLabel = new Label("Paused",labelStyle);


        menuButton = new TextButton("Menu",buttonStyle);
        menuButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((com.oscarboking.arrowhail.GdxGame)Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game,adHandler));
            }
        });

        pausedLabel.setVisible(false);
        slowMoTime.setVisible(false);
        highScoreLabel.setVisible(false);
        holdToTryAgainLabel.setVisible(false);
        menuButton.setVisible(false);
        finalScoreLabel.setVisible(false);
        finalScoreTextLabel.setVisible(false);

        table = new Table();
        table.top();
        table.setFillParent(true);

        table.add(scoreLabel).expandX().align(Align.left).padLeft(20).padTop(10).padRight(20);
        table.row();

        x2 = new Image(new Texture("x2.png"));
        x2.setSize(40,40);
        x2.setVisible(false);

        table.add(x2).width(40).height(40).align(Align.left).padLeft(20).padTop(5);
        table.row();
        table.add(pausedLabel).padTop(20);
        table.row();
        table.add(finalScoreTextLabel).padTop(20);
        table.row();
        table.add(finalScoreLabel).padTop(40);
        table.row();
        table.add(highScoreLabel).padTop(40);
        table.row();
        table.add(holdToStartLabel);
        table.row();
        table.add(holdToTryAgainLabel);
        table.row();
        table.add(menuButton).padTop(50);
        table.row();
        //table.debug();


        slowMo = new Image(new Texture("slow_mo.png"));
        slowMo.setSize(40,40);
        slowMo.setVisible(false);

        Table slowMoTable = new Table();
        slowMoTable.setSize(40,40);
        slowMoTable.align(Align.left);


        slowMoTable.add(slowMo).width(40).height(40).align(Align.left);

        slowMoTable.add(slowMoTime).width(40).height(40).padLeft(5).align(Align.left);
        table.add(slowMoTable).fillX().pad(5).align(Align.left).padTop(80);


        table.row();

        pause = new Image(new Texture("pause.png"));
        pause.setSize(40,40);
        pause.setVisible(false);

        table.add(pause).width(40).height(40).pad(5).align(Align.left);
        table.row();

        shield = new Image(new Texture("shield.png"));
        shield.setSize(40,40);
        shield.setVisible(false);
        //shield.setColor(shield.getColor().r,shield.getColor().g, shield.getColor().b,0.3f);
        table.add(shield).width(40).height(40).pad(5).align(Align.left);
        table.row();

        stage.addActor(table);
    }

    public void showPauseText(boolean value) {
        if (value) {
            pausedLabel.setVisible(true);
        } else {
            pausedLabel.setVisible(false);
        }
    }

    public void update(float dt){
        timeCount+=1;
        if(timeCount >= 1 && timeCount%10==0 && hasStarted){
            score+=scorePerTick;
            scoreLabel.setText(score.toString());
        }
    }

    public void showGameOverText(int arrowsDodgedThisGame, int powerUpsThisGame){

        highScore = prefs.getInteger("highScore",0);

        showx2(false);
        showSlowMo(false);
        showPause(false);
        showShield(false);

        highScoreLabel.setVisible(true);
        holdToTryAgainLabel.setVisible(true);
        menuButton.setVisible(true);
        finalScoreLabel.setText(score.toString());
        finalScoreLabel.setVisible(true);
        finalScoreTextLabel.setVisible(true);

        int newTotalArrows = prefs.getInteger("totalArrowsDodged",0) + arrowsDodgedThisGame;
        int newTotalPowerUps = prefs.getInteger("totalPowerUps",0) + powerUpsThisGame;
        int newTotalTimesPlayed = prefs.getInteger("totalTimesPlayed",0) + 1;
        int coins = prefs.getInteger("coins",0)+score;
        prefs.putInteger("totalArrowsDodged",newTotalArrows);
        prefs.putInteger("totalPowerUps",newTotalPowerUps);
        prefs.putInteger("totalTimesPlayed",newTotalTimesPlayed);
        prefs.putInteger("coins",coins);

        if(score>highScore){
            highScoreLabel.setText("New high score!");
            prefs.putInteger("highScore",score);
        }
        prefs.flush();
        scoreLabel.setVisible(false);
    }

    public void showShield(boolean value){
        if(value){
            //shield.setColor(shield.getColor().r,shield.getColor().g, shield.getColor().b,1f);
            shield.setVisible(true);
        }else{
            //shield.setColor(shield.getColor().r,shield.getColor().g, shield.getColor().b,0.3f);
            shield.setVisible(false);
        }
    }

    public void showPause(boolean value){
        if(value){
            pause.setVisible(true);
        }else{
            pausedLabel.setVisible(false);
            pause.setVisible(false);
        }
    }

    public void showSlowMo(boolean value){
        if(value){
            slowMoLeft=8;
            slowMo.setVisible(true);
            slowMoTime.setVisible(true);
            if(!countingDownSlowMo) {
                countingDownSlowMo=true;
                timer = new Timer();
                timer.schedule(new UpdateTimer(), 0, 1000);
            }
        }else{
            slowMo.setVisible(false);
            slowMoTime.setVisible(false);
        }
    }


    public void showx2(boolean value){
        if(value){
            x2.setVisible(true);
        }else{
            x2.setVisible(false);
        }
    }

    public void hideGameOverText(){
        highScoreLabel.setVisible(false);
        holdToTryAgainLabel.setVisible(false);
        menuButton.setVisible(false);
        finalScoreLabel.setVisible(false);
        finalScoreTextLabel.setVisible(false);

        scoreLabel.setVisible(true);

        score=0;
        timeCount = 0;
    }

    public void setHasStarted(Boolean hasStarted) {
        holdToStartLabel.setVisible(false);
        this.hasStarted = hasStarted;
    }

    public Integer getScore(){
        return this.score;
    }

    public void setScorePerTick(Integer scorePerTick) {
        if(scorePerTick==2){
            x2.setVisible(true);
        }else{
            x2.setVisible(false);
        }
        this.scorePerTick = scorePerTick;
    }

    class UpdateTimer extends TimerTask {
        public void run() {
            if(countingDownSlowMo) {
                slowMo.setVisible(true);
                slowMoTime.setVisible(true);
                slowMoLeft--;
                slowMoTime.setText(slowMoLeft + "");
                if(slowMoLeft==0){
                    this.cancel();
                    countingDownSlowMo=false;
                }
            }

        }
    }

}
