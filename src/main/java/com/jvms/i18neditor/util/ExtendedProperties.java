package com.jvms.i18neditor.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class ExtendedProperties extends Properties {
	private static final long serialVersionUID = 6042931434040718478L;
	
	private final static String LIST_SEPARATOR = ",";
	
	public void load(Path path) {
		if (Files.exists(path)) {
			try (InputStream in = Files.newInputStream(path)) {
				load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void store(Path path) {
		try (OutputStream out = Files.newOutputStream(path)) {
			store(out, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setProperty(String key, List<String> values) {
		setProperty(key, values.stream().collect(Collectors.joining(LIST_SEPARATOR)));
	}
	
	public void setProperty(String key, Integer value) {
		setProperty(key, value == null ? null : value.toString());
	}
	
	public List<String> getListProperty(String key) {
		String value = getProperty(key);
		return value == null ? Lists.newLinkedList() : Lists.newLinkedList(Arrays.asList(value.split(LIST_SEPARATOR)));
	}
	
	public Integer getIntegerProperty(String key) {
		String value = getProperty(key);
		return value != null && !value.isEmpty() ? Integer.parseInt(value) : null;
	}
	
	public Integer getIntegerProperty(String key, Integer defaultValue) {
		Integer value = getIntegerProperty(key); 
		return value != null ? value : defaultValue;
	}
}