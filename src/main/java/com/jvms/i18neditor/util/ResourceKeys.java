package com.jvms.i18neditor.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.jvms.i18neditor.Resource;

/**
 * This class provides translation key utility functions for a {@link Resource}.
 * 
 * <p>A translation key is a {@code String} consisting of one or more parts separated by a dot.<br>
 * A key starting or ending with a dot or a key containing white spaces is considered to be invalid.<p> 
 * 
 * @author Jacob van Mourik
 */
public final class ResourceKeys {
	
	/**
	 * See {@link #create(List)}.
	 */
	public static String create(String... parts) {
		return create(Arrays.asList(parts));
	}
	
	/**
	 * Creates a key by joining the given parts.
	 * 
	 * @param 	parts the parts of the key.
	 * @return 	the created key.
	 */
	public static String create(List<String> parts) {
		return parts.stream().filter(p -> p != null && !p.isEmpty()).collect(Collectors.joining("_"));
	}
	
	/**
	 * Checks whether the given key is a valid key.
	 * A key starting or ending with a dot or a key containing white spaces is considered to be invalid.
	 * 
	 * @param 	key the key to validate.
	 * @return	whether the key is valid or not.
	 */
	public static boolean isValid(String key) {
		return !key.isEmpty() && !key.startsWith("_") && !key.endsWith("_") && key.matches("[^\\s]+");
	}
	
	/**
	 * Returns the size of a key. 
	 * The size is the number of parts the key consists of.
	 * 
	 * @param 	key the key.
	 * @return	the number of parts the key consists of.
	 */
	public static int size(String key) {
		return parts(key).length;
	}
	
	/**
	 * Returns the parts of a key.
	 * 
	 * @param 	key the key.
	 * @return	the parts of the key.
	 */
	public static String[] parts(String key) {
		return key.split("_");
	}
	
	/**
	 * Returns the last parts of a key with a given offset.
	 * The offset must lie between zero and the total number of parts, inclusive.
	 * 
	 * @param 	key the key.
	 * @param 	offset the number of parts to skip from the beginning.
	 * @return	the last sub parts.
	 */
	public static String[] subParts(String key, int offset) {
		String[] parts = parts(key);
		return Arrays.copyOfRange(parts, offset, parts.length);
	}
	
	/**
	 * Returns the first part of a key.
	 * 
	 * @param 	key the key.
	 * @return 	the first part.
	 */
	public static String firstPart(String key) {
		return parts(key)[0];
	}
	
	/**
	 * Returns the last part of a key.
	 * 
	 * @param 	key the key.
	 * @return 	the last part.
	 */
	public static String lastPart(String key) {
		String[] parts = parts(key);
		return parts[parts.length-1];
	}
	
	/**
	 * Creates a new key consisting of all but the first part.
	 * If the key has only one part, an empty key will be returned.
	 * 
	 * @param 	key the key.
	 * @return	the key without the first part.
	 */
	public static String withoutFirstPart(String key) {
		if (size(key) == 1) return "";
		return key.replaceAll("^[^.]+_", "");
	}
	
	/**
	 * Creates a new key consisting of all but the last part.
	 * If the key has only one part, an empty key will be returned.
	 * 
	 * @param 	key the key.
	 * @return	the key without the last part.
	 */
	public static String withoutLastPart(String key) {
		if (size(key) == 1) return "";
		return key.replaceAll("_[^.]+$", "");
	}
	
	/**
	 * Retrieve the part of the given key which is a child part of the given parent key.
	 * A key is a child of another key if it has the same parts at the beginning as the other key.
	 * This function will only return the child parts, so without the beginning parent parts.
	 * 
	 * <p>If the resulting key is the same as the given key, the key is considered not to be a child 
	 * of the given parent key, so an empty key will be returned.</p>
	 * 
	 * @param 	key the original key.
	 * @param 	parentKey a possible parent key of the original key.
	 * @return 	the part of the given key which is a child of the given parent key.
	 */
	public static String childKey(String key, String parentKey) {
		if (key == null || key.isEmpty()) return "";
		if (parentKey == null || parentKey.isEmpty()) return key;
		String result = key.replaceFirst(parentKey + "_", "");
		if (result.equals(key)) return "";
		return result;
	}
	
	/**
	 * Checks whether the given key is a child key of the given parent key.
	 * A key is a child of another key if it has the same parts at the beginning as the other key.
	 * 
	 * @param 	key the original key.
	 * @param 	parentKey the possible parent key of the original key.
	 * @return	whether the given key is a child of the given parent key.
	 */
	public static boolean isChildKeyOf(String key, String parentKey) {
		return key.startsWith(parentKey + "_");
	}
	
	/**
	 * Gets the unique root keys of a list of keys. A root key is the first part of a key.
	 * 
	 * @param 	keys the keys to retrieve the unique root keys from.
	 * @return 	the unique root keys.
	 */
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
	
	/**
	 * Gets all keys from the given key list which are a child of the given parent key.
	 * A key is a child of another key if it has the same parts at the beginning as the other key.
	 * 
	 * @param 	keys the keys to retrieve the child keys from.
	 * @param 	parentKey the parent key.
	 * @return	the keys of the given key list which are a child of the given parent key.
	 */
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
