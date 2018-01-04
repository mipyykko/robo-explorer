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
		while (!Button.ESCAPE.isPressed()) {
			String[] configKeys = config.getConfigKeys();
			TextMenu configMenu = new TextMenu(configKeys, 1, "Config");
			int menuChoice = configMenu.select();
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
		Screen.showText("this is a rather long error message with spam and asdfsdfsdfdfdfdfdfdf", 0, 0);
		while (!Button.ESCAPE.isPressed()) { Thread.yield(); }	
	}
	
	private String motorPortMenu(String port) {
		while (!Button.ESCAPE.isPressed()) {
			LCD.clear();
			LCD.drawString("current: " + config.get(port), 0, 1);
			TextMenu portMenu = new TextMenu(new String[]{"A", "B", "C"}, 2, port);
			return "" + 'A' + portMenu.select();
		}
		return config.get(port);
	}
	
	private String sensorPortMenu(String port) {
		while (!Button.ESCAPE.isPressed()) {
			LCD.clear();
			LCD.drawString("current: S" + config.get(port), 0, 1);
			TextMenu portMenu = new TextMenu(new String[]{"S1", "S2", "S3", "S4"}, 2, port);
			return "" + 1 + portMenu.select();
		}
		return config.get(port);
	}
}
