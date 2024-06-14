package com.mygdx.wartowers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.wartowers.Bluetooth.BluetoothServiceInterface;
import com.mygdx.wartowers.database.FireStoreInterface;
import com.mygdx.wartowers.states.GameStateManager;
import com.mygdx.wartowers.states.LoginState;
import com.mygdx.wartowers.utils.Constants;


public class WarTowers extends Game {
	SpriteBatch batch;
	GameStateManager gsm;
	public static FireStoreInterface dbInterface;
	public static BluetoothServiceInterface bluetoothService;

	public WarTowers() {
	}
	public WarTowers(FireStoreInterface dbInterface, BluetoothServiceInterface bluetoothService) {
		WarTowers.dbInterface = dbInterface;
		WarTowers.bluetoothService = bluetoothService;
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
		Constants.warriors_speed = new int[]{(int)(screenWidth / 5.0f), (int)(screenWidth / 5.0f)};

		Gdx.app.log("Screen Size", "Width: " + screenWidth + ", Height: " + screenHeight);

		gsm.push(new LoginState(gsm));
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
