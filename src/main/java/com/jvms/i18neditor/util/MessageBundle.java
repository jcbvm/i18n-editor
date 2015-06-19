package com.jvms.i18neditor.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class MessageBundle {
	private final static ResourceBundle RESOURCES;
	
	static {
		RESOURCES = ResourceBundle.getBundle("bundles/messages", new Locale("nl"));//Locale.getDefault());
	}
	
	public static String get(String key, Object... args) {
		String value = RESOURCES.getString(key);
		return MessageFormat.format(value, args);
	}
	
	public static Character getMnemonic(String key) {
		String value = RESOURCES.getString(key);
		return value.charAt(0);
	}
}