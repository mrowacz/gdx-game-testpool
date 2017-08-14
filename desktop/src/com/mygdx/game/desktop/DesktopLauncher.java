package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = MyGdxGame.TITLE;
		config.width = MyGdxGame.V_WIDTH * MyGdxGame.SCALE;
		config.width = MyGdxGame.V_HEIGHT * MyGdxGame.SCALE;

		new LwjglApplication(new MyGdxGame(), config);
	}
}
