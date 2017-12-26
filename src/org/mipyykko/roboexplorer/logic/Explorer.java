package org.mipyykko.roboexplorer.logic;

import org.mipyykko.roboexplorer.config.Config;
import org.mipyykko.roboexplorer.ui.Menu;

public class Explorer {

	private Config config;
	private Menu menu;
	
	public Explorer() {
		this.config = new Config();
		this.menu = new Menu(this.config);
	}
	
	public void run() {
		while (menu.startMenu() >= 0) {
			// nawt
		}
	}
}
