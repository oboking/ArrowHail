package com.oscarboking.arrowhail.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.oscarboking.arrowhail.AdHandler;
import com.oscarboking.arrowhail.GdxGame;

public class StatScreen implements Screen{

    private Preferences prefs;

    private SpriteBatch batch;
    private TextureAtlas buttonAtlas;
    private TextButton.TextButtonStyle buttonStyle;
    private Skin skin;
    private BitmapFont font;
    private Label.LabelStyle labelStyle;
    private Stage stage;
    private Table table;
    private FreeTypeFontGenerator generator;

    private Label highScore;
    private Label totalArrowsDodged;
    private Label totalPowerUpsActivated;
    private Label totalTimesPlayed;
    private TextButton backButton;

    private GdxGame game;
    private AdHandler adHandler;

    public StatScreen(GdxGame game,AdHandler handler) {
        this.game = game;
        this.adHandler=handler;
        prefs = Gdx.app.getPreferences("prefs");
    }

    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        //font = new BitmapFont(Gdx.files.internal("font.ttf"));


        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;

        font=generator.generateFont(parameter);
        generator.dispose();

        skin = new Skin();
        buttonAtlas = new TextureAtlas("button.pack");
        skin.addRegions(buttonAtlas);
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.getDrawable("button_up");
        buttonStyle.over = skin.getDrawable("button_down");
        buttonStyle.down = skin.getDrawable("button_down");
        buttonStyle.font = font;
        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        highScore = new Label("High score: " + Integer.toString(prefs.getInteger("highScore",0)),labelStyle);
        totalArrowsDodged = new Label("Total arrows dodged: " + Integer.toString(prefs.getInteger("totalArrowsDodged",0)),labelStyle);
        totalPowerUpsActivated = new Label("Total powerups activated: " + Integer.toString(prefs.getInteger("totalPowerUps",0)),labelStyle);
        totalTimesPlayed = new Label("Times played: " + Integer.toString(prefs.getInteger("totalTimesPlayed",0)),labelStyle);

        prefs.flush();
        backButton = new TextButton("Back",buttonStyle);

        backButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new com.oscarboking.arrowhail.Screens.MenuScreen(game,adHandler));
                return true;
            }
        });
        backButton.align(Align.center);


        table = new Table();
        table.setFillParent(true);
        table.add(highScore).row();
        table.add(totalTimesPlayed).padTop(80).row();
        table.add(totalArrowsDodged).padTop(80).row();
        table.add(totalPowerUpsActivated).padTop(80).row();
        table.row();
        table.add(backButton).padTop(150).center();

        stage.addActor(table);


    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(40 / 255f, 37 / 255f, 44 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        batch.begin();
        stage.draw();
        batch.end();

    }

    @Override
    public void resize(int width, int height) {

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
