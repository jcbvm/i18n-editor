package com.jvms.i18neditor.util;

import java.awt.Color;

/**
 * This class provides utility functions for colors.
 * 
 * @author Jacob
 */
public final class Colors {

	/**
	 * Scales the RGB values of a color with the given factor.
	 * 
	 * @param 	c the original color
	 * @param 	factor the factor to scale with
	 * @return 	the scaled color
	 */
	public static Color scale(Color c, float factor) {
		return new Color(
				roundColorValue(c.getRed()*factor), 
				roundColorValue(c.getGreen()*factor), 
				roundColorValue(c.getBlue()*factor),
				c.getAlpha());
	}
	
	private static int roundColorValue(float value) {
		return Math.round(Math.max(0, Math.min(255, value)));
	}
}
