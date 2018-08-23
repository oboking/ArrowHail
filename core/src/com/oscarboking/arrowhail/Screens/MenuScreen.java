package com.oscarboking.arrowhail.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.beans.Visibility;


public class MenuScreen implements Screen {

    private SpriteBatch batch;
    protected Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    protected Skin skin;
    private TextButton.TextButtonStyle buttonStyle;
    private TextButton.TextButtonStyle buttonDisabledStyle;
    private FreeTypeFontGenerator generator;
    private BitmapFont font;
    public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private Label.LabelStyle labelStyle;
    private Label titleLabel;
    private com.oscarboking.arrowhail.GdxGame game;

    private FreeTypeFontGenerator generatorBig;
    private BitmapFont fontBig;
    private Label.LabelStyle labelStyleBig;
    private FreeTypeFontGenerator generatorSmall;
    private BitmapFont fontSmall;
    private Label.LabelStyle labelStyleSmall;


    //background stuff
    Texture background1, background2;
    float yMax, yCoordBg1, yCoordBg2;
    final int BACKGROUND_MOVE_SPEED = 200; // pixels per second. Put your value here.

    private TextButton campaignButton;
    private TextButton playButton;
    private TextButton collectionButton;
    private TextButton statButton;
    private ImageButton helpButton;
    private Image coinImage;
    private Image helpImage;
    private Label coinLabel;
    private Table helpTable;
    private Table mainTable;
    private Boolean isFirstTime = false;
    private Boolean clickedPlay = false;

    private com.oscarboking.arrowhail.AdHandler adHandler;
    private Preferences prefs;


    public MenuScreen(com.oscarboking.arrowhail.GdxGame game, com.oscarboking.arrowhail.AdHandler handler)
    {
        this.game = game;
        this.adHandler = handler;
        prefs = Gdx.app.getPreferences("prefs");
    }

    @Override
    public void show() {
        boolean firstTime = prefs.getBoolean("isFirstTime",true);
        if(firstTime){
            prefs.putBoolean("isFirstTime",false);
            isFirstTime=true;
        }
        adHandler.showAds(true);
        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;

        font=generator.generateFont(parameter);
        generator.dispose();

        generatorBig = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterBig = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterBig.size = 44;

        fontBig=generatorBig.generateFont(parameterBig);
        generatorBig.dispose();
        labelStyleBig = new Label.LabelStyle(fontBig,Color.WHITE);

        generatorSmall = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterSmall = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterSmall.size = 14;
        parameterSmall.color=new Color(255,255,255,0.5f);

        fontSmall=generatorSmall.generateFont(parameterSmall);
        generatorSmall.dispose();
        labelStyleSmall = new Label.LabelStyle(fontSmall,Color.WHITE);

        skin = new Skin();
        atlas = new TextureAtlas("button.pack");
        skin.addRegions(atlas);
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.getDrawable("button_up");
        buttonStyle.over = skin.getDrawable("button_down");
        buttonStyle.down = skin.getDrawable("button_down");
        buttonStyle.font = font;

        buttonDisabledStyle = new TextButton.TextButtonStyle();
        buttonDisabledStyle.up = skin.getDrawable("button_soon");
        buttonDisabledStyle.over = skin.getDrawable("button_down");
        buttonDisabledStyle.down = skin.getDrawable("button_down");
        buttonDisabledStyle.font = font;


        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        titleLabel = new Label("Arrow Hail",labelStyleBig);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(com.oscarboking.arrowhail.GdxGame.V_WIDTH, com.oscarboking.arrowhail.GdxGame.V_HEIGHT, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();


        //background stuff
        background1 = new Texture(Gdx.files.internal("hexa_background.png"));
        background2 = new Texture(Gdx.files.internal("hexa_background.png")); // identical
        yMax = 957;
        yCoordBg1 = yMax; yCoordBg2 = 0;

        stage = new Stage(viewport, batch);

        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

        //Create buttons
        campaignButton = new TextButton("Campaign",buttonDisabledStyle);
        playButton = new TextButton("Play", buttonStyle);
        collectionButton = new TextButton("Collection", buttonDisabledStyle);
        statButton = new TextButton("Stats", buttonStyle);
        //TextButton exitButton = new TextButton("Exit", buttonStyle);

        campaignButton.setDisabled(true);

        //Add listeners to buttons
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickedPlay=true;
                if(isFirstTime){
                    helpTable.setVisible(true);
                    mainTable.setVisible(false);
                }else {
                    adHandler.showAds(false);
                    ((com.oscarboking.arrowhail.GdxGame) Gdx.app.getApplicationListener()).setScreen(new PlayScreen(game, adHandler));
                }
            }
        });
        statButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((com.oscarboking.arrowhail.GdxGame)Gdx.app.getApplicationListener()).setScreen(new StatScreen(game,adHandler));
            }
        });
        collectionButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((com.oscarboking.arrowhail.GdxGame)Gdx.app.getApplicationListener()).setScreen(new CollectionScreen(game,adHandler));
            }
        });
        /*exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });*/
        Drawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture("help.png")));
        helpButton = new ImageButton(drawable);

        helpImage = new Image(new Texture("howtoplay.png"));

        coinImage = new Image(new Texture("coin.png"));
        coinLabel = new Label(""+prefs.getInteger("coins",0),labelStyleSmall);
        prefs.flush();

        Table coinTable = new Table();
        coinTable.add(helpButton).size(55,55).padRight(185).padTop(15);
        coinTable.add(coinImage).size(30,30).padLeft(185);
        coinTable.add(coinLabel).padLeft(5);


        //Create Table
        mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();

        campaignButton.setTouchable(Touchable.disabled);
        collectionButton.setTouchable(Touchable.disabled);

        //Add buttons to table
        //mainTable.debug();
        mainTable.add(coinTable).expandX();
        mainTable.row();
        mainTable.add(titleLabel).padTop(100);
        mainTable.row();
        mainTable.add(playButton).padTop(120).size(300, Gdx.graphics.getHeight() / 24).row();
        mainTable.add(new Label("Coming soon!",labelStyleSmall)).padTop(20).row();
        mainTable.add(collectionButton).size(300, Gdx.graphics.getHeight() / 24).row();
        mainTable.row();
        mainTable.add(statButton).padTop(40).size(300, Gdx.graphics.getHeight() / 24).row();
        mainTable.row();
        //mainTable.add(exitButton).padTop(40);

        helpTable = new Table();
        helpTable.setFillParent(true);
        helpTable.add(helpImage).size(480,700);
        helpTable.setVisible(false);
        stage.addActor(helpTable);

        helpTable.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(isFirstTime&&clickedPlay){
                    adHandler.showAds(false);
                    ((com.oscarboking.arrowhail.GdxGame)Gdx.app.getApplicationListener()).setScreen(new PlayScreen(game,adHandler));
                }else {
                    helpTable.setVisible(false);
                    mainTable.setVisible(true);
                }
            }
        });

        helpButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainTable.setVisible(false);
                helpTable.setVisible(true);
            }
        });


        //Add table to stage
        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //move background
        yCoordBg1 -= BACKGROUND_MOVE_SPEED * Gdx.graphics.getDeltaTime();
        yCoordBg2 = yCoordBg1 - yMax;  // We move the background, not the camera
        if (yCoordBg1 < 0) {
            yCoordBg1 = yMax; yCoordBg2 = 0;
        }

        stage.getBatch().begin();
        stage.getBatch().draw(background1,0,yCoordBg1);
        stage.getBatch().draw(background2,0,yCoordBg2);
        stage.getBatch().end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
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
        skin.dispose();
        atlas.dispose();
    }
}