package com.jvms.i18neditor.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public final class SettingsBundle {
	private final static Path FILE_PATH;
	
	static {
		FILE_PATH = Paths.get(System.getProperty("user.home"), ".i18n-editor");
	}
	
	public static void store(String key, String value) {
		try {
			OutputStream out = Files.newOutputStream(FILE_PATH);
			InputStream in = Files.newInputStream(FILE_PATH);
			Properties props = new Properties();
			props.load(in);
			props.setProperty(key, value);
			props.store(out, null);
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void store(String key, List<String> values) {
		store(key, values.stream().collect(Collectors.joining(",")));
	}
	
	public static String get(String key) {
		String result = null;
		if (Files.exists(FILE_PATH)) {
			try {
				InputStream in = Files.newInputStream(FILE_PATH);
				Properties props = new Properties();
				props.load(in);
				result = props.getProperty(key);
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static List<String> getAsList(String key) {
		String value = get(key);
		return value == null ? Lists.newLinkedList() : Lists.newArrayList(Arrays.asList(value.split(",")));
	}
}