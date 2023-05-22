package com.mygdx.wartowers;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.wartowers.WarTowers;
import com.mygdx.wartowers.utils.Constants;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(Constants.APP_WIDTH, Constants.APP_HEIGHT);

		config.setTitle(Constants.APP_TITLE);
		new Lwjgl3Application(new WarTowers(), config);
	}
}
