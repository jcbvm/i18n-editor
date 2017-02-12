package com.jvms.i18neditor.util;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * This class provides utility functions for loading images.
 * 
 * @author Jacob van Mourik
 */
public final class Images {

	/**
	 * Loads an image icon from the current classpath.
	 * 
	 * @param 	path the path of the image to load
	 * @return 	the image icon
	 */
	public static ImageIcon loadFromClasspath(String path) {
		return new ImageIcon(getClasspathURL(path));
	}
	
	/**
	 * Gets the URL of an image from the current classpath.
	 * 
	 * @param 	path the path of the image to load
	 * @return 	the image icon
	 */
	public static URL getClasspathURL(String path) {
		return Images.class.getClassLoader().getResource(path);
	}
}
