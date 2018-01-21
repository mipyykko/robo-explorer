package org.mipyykko.roboexplorerpc;

import javax.swing.SwingUtilities;

import org.mipyykko.roboexplorerpc.gui.GUI;

/**
 * Käynnistää GUIn.
 * 
 * @author mipyykko
 *
 */
public class Main {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GUI();
			}
		});
	}

}
