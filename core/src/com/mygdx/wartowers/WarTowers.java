package com.mygdx.wartowers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.wartowers.states.GameStateManager;
import com.mygdx.wartowers.states.MenuState;
import com.mygdx.wartowers.utils.Constants;


public class WarTowers extends Game {
	SpriteBatch batch;
	GameStateManager gsm;
	DatabaseInterface dbInterface;

	public WarTowers() {
	}
	public WarTowers(DatabaseInterface dbInterface) {
		this.dbInterface = dbInterface;
	}
	@Override
	public void create () {
		batch = new SpriteBatch();
		gsm = new GameStateManager();
		ScreenUtils.clear(1, 0, 0, 1);

		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		Constants.APP_WIDTH = screenWidth;
		Constants.APP_HEIGHT = screenHeight;

		Gdx.app.log("Screen Size", "Width: " + screenWidth + ", Height: " + screenHeight);
//		dbInterface.addScore("Pan", 8);
//		dbInterface.addScore("Pan2", 8);
//		ScoreEntry s = dbInterface.getScore("Pan2");
//		System.out.println(s.getId());
		gsm.push(new MenuState(gsm, dbInterface));

	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
