package com.jvms.i18neditor.util;

import javax.swing.ImageIcon;

/**
 * This class provides utility functions for loading images.
 * 
 * @author Jacob
 */
public final class Images {

	/**
	 * Loads an image icon from the current classpath.
	 * 
	 * @param 	path the path of the image to load
	 * @return 	the image icon
	 */
	public static ImageIcon getFromClasspath(String path) {
		return new ImageIcon(Images.class.getClassLoader().getResource(path));
	}
}
