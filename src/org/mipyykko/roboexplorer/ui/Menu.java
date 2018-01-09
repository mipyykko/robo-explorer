package org.mipyykko.roboexplorer.ui;
import lejos.nxt.Button;
import lejos.nxt.LCD;
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
	
	public void configMenu() {
		int currentChoice = 0;
		while (!Button.ESCAPE.isPressed()) {
			String[] configKeys = config.getConfigKeys();
			TextMenu configMenu = new TextMenu(configKeys, 1, "Config");
			int menuChoice = configMenu.select(currentChoice);
			currentChoice = menuChoice;
			String selectedKey = configKeys[menuChoice];
			if (selectedKey.indexOf("Motor") >= 0) {
				String motorPortChoice = motorPortMenu(selectedKey);
				config.set(selectedKey, motorPortChoice);
			} else if (selectedKey.indexOf("Sensor") >= 0) {
				String sensorPortChoice = sensorPortMenu(selectedKey);
				config.set(selectedKey, sensorPortChoice);
			} else {
				// float value select
			}
		}
		Screen.showText("this is a rather long error message for testing", 0, 0);
		while (!Button.ESCAPE.isPressed()) { Thread.yield(); }	
	}
	
	private String motorPortMenu(String port) {
		while (!Button.ESCAPE.isPressed()) {
			LCD.clear();
			LCD.drawString("current: " + config.get(port), 0, 0);
			TextMenu portMenu = new TextMenu(new String[]{"A", "B", "C"}, 2, port);
			int currentChoice = (int) (config.get(port).charAt(0) - 'A');
			int selection = portMenu.select(currentChoice);
			if (selection >= 0 && selection <= 2) {
				return "" + 'A' + selection;
			}
		}
		return config.get(port);
	}
	
	private String sensorPortMenu(String port) {
		while (!Button.ESCAPE.isPressed()) {
			LCD.clear();
			LCD.drawString("current: " + config.get(port), 0, 0);
			TextMenu portMenu = new TextMenu(new String[]{"S1", "S2", "S3", "S4"}, 2, port);
			int currentChoice = Integer.parseInt("" + config.get(port).charAt(1)) - 1;
			int selection = portMenu.select(currentChoice);
			if (selection >= 0 && selection <= 3) {
				return "S" + 1 + selection;
			}
		}
		return config.get(port);
	}
}
