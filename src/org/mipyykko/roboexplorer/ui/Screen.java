package org.mipyykko.roboexplorer.ui;

import lejos.nxt.Button;
import lejos.nxt.LCD;

import org.mipyykko.roboexplorer.util.Text;

/**
 * Näytölle kirjoittamiseen ja piirtämiseen tarkoitetut metodit.
 * 
 * @author mipyykko
 *
 */
public class Screen {

	public Screen() {}
	
	/**
	 * Näyttää annetun tekstin rivitettynä ruudulla ja odottaa näppäimen painallusta.
	 * 
	 * @param text
	 * @param x
	 * @param y
	 */
	public static void showText(String text, int x, int y) {
		LCD.clear();
		String[] lines = new Text(text).flowTextLines(18 - x);
		for (int line = 0; line < lines.length; line++) {
			LCD.drawString(lines[line], x, y + line);
		}
		
		Button.waitForPress();
	}
}
