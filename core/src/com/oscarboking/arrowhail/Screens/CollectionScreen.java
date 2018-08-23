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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.oscarboking.arrowhail.AdHandler;
import com.oscarboking.arrowhail.GdxGame;

import java.util.Hashtable;

public class CollectionScreen implements Screen{


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


    private Label totalArrowsDodged;
    private Label totalPowerUpsActivated;
    private Label totalTimesPlayed;
    private TextButton backButton;

    private Table backgroundsTable;

    private TextButton backgroundsButton;

    private GdxGame game;
    private ScrollPane scrollPane;

    private AdHandler adHandler;

    public CollectionScreen(GdxGame game,AdHandler adHandler) {
        this.game = game;
        this.adHandler = adHandler;
        prefs = Gdx.app.getPreferences("My Preferences");
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
        buttonStyle.up = skin.getDrawable("button");
        buttonStyle.over = skin.getDrawable("button_down");
        buttonStyle.down = skin.getDrawable("button_down");
        buttonStyle.font = font;
        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        totalArrowsDodged = new Label("Total arrows dodged: " + Integer.toString(prefs.getInteger("totalArrowsDodged",0)),labelStyle);

        backgroundsButton = new TextButton("Backgrounds",buttonStyle);
        backButton = new TextButton("Back",buttonStyle);


        backButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new com.oscarboking.arrowhail.Screens.MenuScreen(game,adHandler));
                return true;
            }
        });
        backButton.align(Align.center);

        backgroundsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        backgroundsTable = new Table();
        addBackgrounds();

        table = new Table();

        table.debug();
        table.setFillParent(true);
        table.top();
        table.add(backgroundsButton).row();
        table.add(backgroundsTable).row();
        table.row();
        table.add(backButton).padTop(150).colspan(3).center();

        stage.addActor(table);


    }

    //add backgrounds to backgroundsTable
    public void addBackgrounds(){

        com.oscarboking.arrowhail.Buyables[] backgrounds = {};
        Json json = new Json();

        String serializedBackgrounds = Gdx.app.getPreferences("My Preferences").getString("backgrounds","");


        if(serializedBackgrounds.equals("")){//first time opening collection, add the stuff to preferences
            Hashtable<String, String> hashTable = new Hashtable<String, String>();


            hashTable.put("backgrounds", json.toJson(backgrounds) ); //here you are serializing the array

            //putting the map of backgrounds into preferences
            prefs.put(hashTable);
            prefs.flush();
        }else{
            com.oscarboking.arrowhail.Buyables[] deserializedBackgrounds = json.fromJson(com.oscarboking.arrowhail.Buyables[].class, serializedBackgrounds); //you need to pass the class type - be aware of it!

        }


        //gettings backgrounds from prefs

        backgroundsTable.add();
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
