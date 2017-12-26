package org.mipyykko.roboexplorer.ui;
import org.mipyykko.roboexplorer.config.Config;

import lejos.nxt.Button;
import lejos.util.TextMenu;

public class Menu {

	private Config config;
	
	public Menu(Config config) {
		this.config = config;
	}
	
	public static int startMenu() {
		while (!Button.ESCAPE.isPressed()) {
			TextMenu startMenu = new TextMenu(new String[]{"Start", "Config"}, 1, "Robo Explorer");
			int menuChoice = startMenu.select();
			switch (menuChoice) {
				case 0:
					return 0;
				case 1:
					configMenu();
					break;
				default:
					break;
			}
		}
		return -1;
	}
	
	public static void configMenu() {
		
	}
}
