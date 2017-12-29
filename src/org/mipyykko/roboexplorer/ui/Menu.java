package org.mipyykko.roboexplorer.ui;
import lejos.nxt.Button;
import lejos.util.TextMenu;

import org.mipyykko.roboexplorer.config.Config;
import org.mipyykko.roboexplorer.util.RoboException;

/**
 * Valikko ja siihen liittyvät toiminnot.
 * 
 * @author mipyykko
 *
 */
public class Menu {

	private Config config;
	
	public Menu(Config config) {
		this.config = config;
	}
	
	/**
	 * Alkuvalikko.
	 * 
	 * @return 0 jos on valittu Start, muuten -1
	 */
	public int startMenu() {
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
		// debug 
		Screen.showText("this is a rather long error message with spam and asdfsdfsdfdfdfdfdfdf", 0, 0);
		while (!Button.ESCAPE.isPressed()) { Thread.yield(); }	
	}
}
