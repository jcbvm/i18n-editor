package com.jvms.i18neditor.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class MessageBundle {
	private final static ResourceBundle resource = PropertyResourceBundle.getBundle("bundles/messages", Locale.getDefault());
	
	public static String get(String key, Object... args) {
		String value = resource.getString(key);
		return MessageFormat.format(value, args);
	}
	
	public static Character getMnemonic(String key) {
		String value = resource.getString(key);
		return value.charAt(0);
	}
}