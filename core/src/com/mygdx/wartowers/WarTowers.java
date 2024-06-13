package com.mygdx.wartowers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.wartowers.Bluetooth.BluetoothServiceInterface;
import com.mygdx.wartowers.database.DataHolderClass;
import com.mygdx.wartowers.database.FireStoreInterface;
import com.mygdx.wartowers.sprites.BattleResult;
import com.mygdx.wartowers.sprites.PlayerData;
import com.mygdx.wartowers.states.GameStateManager;
import com.mygdx.wartowers.states.LoginState;
import com.mygdx.wartowers.utils.Constants;


public class WarTowers extends Game {
	SpriteBatch batch;
	GameStateManager gsm;
	FireStoreInterface dbInterface;
	BluetoothServiceInterface bluetoothService;

	public WarTowers() {
	}
	public WarTowers(FireStoreInterface dbInterface, BluetoothServiceInterface bluetoothService) {
		this.dbInterface = dbInterface;
		this.bluetoothService = bluetoothService;
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

//		testDBFunction();

		gsm.push(new LoginState(gsm, dbInterface, bluetoothService));
	}

	private void testDBFunction(){
//		dbInterface.addPlayer(new PlayerData("Sasha", 4, 7));
		BattleResult btr = new BattleResult("Sasha", "Kesha", "Sasha");
		dbInterface.updateBattleResult(btr);
		try {
			// Sleep for 3 seconds (3000 milliseconds)
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// Handle interrupted exception
			e.printStackTrace();
		}
		PlayerData playerData = new PlayerData();
//		dbInterface.getPlayerStats("Sasha", playerData);
		DataHolderClass dataHolder = new DataHolderClass();
//		dbInterface.getTopPlayers(dataHolder);
		try {
			// Sleep for 3 seconds (3000 milliseconds)
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// Handle interrupted exception
			e.printStackTrace();
		}
		Array<PlayerData> playerDataArray = dataHolder.getPlayerDataArray();
		for (PlayerData pd : playerDataArray) {
			System.out.println(pd.getName() + "  wins: " + pd.getWins() + "   total: " + pd.getGamesPlayed());
		}
		try {
			// Sleep for 3 seconds (3000 milliseconds)
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// Handle interrupted exception
			e.printStackTrace();
		}

		System.out.println(playerData.getName() + " wins" + playerData.getWins() + " total" + playerData.getGamesPlayed());
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
