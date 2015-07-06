package com.jvms.i18neditor.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public final class TranslationKeys {
	
	public static String create(String... parts) {
		return create(Arrays.asList(parts));
	}
	
	public static String create(List<String> parts) {
		return parts.stream().filter(p -> p != null && !p.isEmpty()).collect(Collectors.joining("."));
	}
	
	public static boolean isValid(String key) {
		return !key.isEmpty() && !key.startsWith(".") && !key.endsWith(".") && !key.contains(" ");
	}
	
	public static int size(String key) {
		return parts(key).length;
	}
	
	public static String[] parts(String key) {
		return key.split("\\.");
	}
	
	public static String[] subParts(String key, int offset) {
		String[] parts = parts(key);
		return Arrays.copyOfRange(parts, offset, parts.length);
	}
	
	public static String firstPart(String key) {
		return parts(key)[0];
	}
	
	public static String lastPart(String key) {
		String[] parts = parts(key);
		return parts[parts.length-1];
	}
	
	public static String withoutFirstPart(String key) {
		if (size(key) == 1) return "";
		return key.replaceAll("^[^.]+\\.", "");
	}
	
	public static String withoutLastPart(String key) {
		if (size(key) == 1) return "";
		return key.replaceAll("\\.[^.]+$", "");
	}
	
	public static String childKey(String childKey, String parentKey) {
		if (childKey == null || childKey.isEmpty()) return "";
		if (parentKey == null || parentKey.isEmpty()) return childKey;
		String result = childKey.replaceFirst(parentKey + "\\.", "");
		if (result.equals(childKey)) return "";
		return result;
	}
	
	public static boolean isChildKeyOf(String childKey, String parentKey) {
		return childKey.startsWith(parentKey + ".");
	}
	
	public static List<String> uniqueRootKeys(List<String> keys) {
		List<String> result = Lists.newLinkedList();
		keys.forEach(key -> {
			String rootKey = firstPart(key);
			if (!result.contains(rootKey)) {
				result.add(rootKey);
			}
		});
		return result;
	}
	
	public static List<String> extractChildKeys(List<String> keys, String parentKey) {
		List<String> result = Lists.newLinkedList();
		keys.forEach(key -> {
			if (isChildKeyOf(key, parentKey)) {
				result.add(childKey(key, parentKey));
			}
		});
		return result;
	}
}
