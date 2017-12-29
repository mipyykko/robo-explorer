package org.mipyykko.roboexplorer.util;

import lejos.nxt.LCD;

import org.mipyykko.roboexplorer.config.ErrorCode;
import org.mipyykko.roboexplorer.ui.Screen;

public class RoboException extends Exception {

	private String message;
	private ErrorCode code;
	
	public RoboException(String message) {
		super(message);
	}

	public RoboException(String message, /* Throwable cause, */ErrorCode code) {
		super(message/*, cause*/);
		this.message = message;
		this.code = code;
		showError();
	}
	
	public void showError() {
		LCD.clear();
		LCD.drawString("ERROR!", 0, 0);
		Screen.showText(message, 0, 2);
	}
}
