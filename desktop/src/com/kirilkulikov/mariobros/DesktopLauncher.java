package com.kirilkulikov.mariobros;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import main.com.kulikov.Mapper.UserMapper;
import main.com.kulikov.MarioBros;
import main.com.kulikov.Repository.UserRepository;
import main.com.kulikov.connection.ConnectionManager;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(1920, 1080);
		config.setTitle("Super Mario Bros");
		UserMapper userMapper = new UserMapper(); // Instantiate UserMapper accordingly
		ConnectionManager connectionManager = new ConnectionManager(); // Instantiate ConnectionManager accordingly
		UserRepository userRepository = new UserRepository(userMapper, connectionManager);
		new Lwjgl3Application(new MarioBros(), config);
	}
}
