package com.jvms.i18neditor.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class provides utility functions for retrieving translations from a resource bundle.<br>
 * By default it loads translations from {@value #RESOURCES_PATH}.
 * 
 * <p>The locale used is the current value of the default locale for this instance of the Java Virtual Machine.</p>
 * 
 * @author Jacob van Mourik
 */
public final class MessageBundle {
	private final static String RESOURCES_PATH = "bundles/messages";
	private final static ResourceBundle RESOURCES;
	
	static {
		RESOURCES = ResourceBundle.getBundle(RESOURCES_PATH, Locale.getDefault());
	}
	
	/**
	 * Gets a value from this bundle for the given {@code key}. Any second arguments will
	 * be used to format the value.
	 * 
	 * @param 	key the bundle key
	 * @param 	args objects used to format the value.
	 * @return 	the formatted value for the given key.
	 */
	public static String get(String key, Object... args) {
		String value = RESOURCES.getString(key);
		return MessageFormat.format(value, args);
	}
	
	/**
	 * Gets a mnemonic value from this bundle.
	 * 
	 * @param 	key the bundle key.
	 * @return 	the mnemonic value for the given key.
	 */
	public static Character getMnemonic(String key) {
		String value = RESOURCES.getString(key);
		return value.charAt(0);
	}
}