package org.mipyykko.roboexplorerpc;

import javax.swing.SwingUtilities;

import org.mipyykko.roboexplorerpc.gui.GUI;

public class Main {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GUI();
			}
		});
	}

}
