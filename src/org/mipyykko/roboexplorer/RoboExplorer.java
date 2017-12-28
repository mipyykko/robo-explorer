package org.mipyykko.roboexplorer;

import org.mipyykko.roboexplorer.config.Config;
import org.mipyykko.roboexplorer.logic.Explorer;
import org.mipyykko.roboexplorer.ui.Menu;

/**
 * Example leJOS Project with an ant build file
 *
 */
public class RoboExplorer {

	public static void main(String[] args) {
		Explorer e = new Explorer();
		e.start();
	}
}
