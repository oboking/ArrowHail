package com.oscarboking.arrowhail;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.oscarboking.arrowhail.Screens.MenuScreen;

public class GdxGame extends Game {
	public static final int V_WIDTH=540;
	public static final int V_HEIGHT=960;
	public static final float PPM = 100;
	public SpriteBatch batch;

	com.oscarboking.arrowhail.AdHandler handler;

	public GdxGame(com.oscarboking.arrowhail.AdHandler handler){
		this.handler=handler;
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new MenuScreen(this,handler));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		batch.dispose();
	}
}
