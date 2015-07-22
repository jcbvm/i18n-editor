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

/**
 * This class extends {@link Properties}.
 * 
 * <p>This implementation adds the ability to load and store properties from a given file path  
 * and adds support to retrieve and store {@code Integer} and {@code List} values.</p>
 * 
 * <p>This implementation also adds extended functionality like {@link #containsKeys(String...)}.</p>
 * 
 * @author Jacob
 */
public class ExtendedProperties extends Properties {
	private static final long serialVersionUID = 6042931434040718478L;
	
	protected String separator;
	
	/**
     * Creates an empty property list with no default values and "," as list separator.
     */
	public ExtendedProperties() {
		this(null, ",");
	}
	
	/**
     * Creates an empty property list with no default values and the specified list separator.
     * 
     * @param	separator the separator used for storing list values.
     */
	public ExtendedProperties(String separator) {
		this(null, separator);
	}
	
	/**
     * Creates an empty property list with the specified defaults and "," as list separator.
     * 
     * @param	defaults the defaults.
     */
    public ExtendedProperties(Properties defaults) {
        this(defaults, ",");
    }
	
	/**
     * Creates an empty property list with the specified defaults and list separator.
     * 
     * @param	defaults the defaults.
     * @param	separator the separator used for storing list values.
     */
    public ExtendedProperties(Properties defaults, String separator) {
        super(defaults);
        this.separator = separator;
    }
	
	/**
	 * Reads a property list from the given file path.
	 * 
	 * @param 	path the path to the property file.
	 */
	public void load(Path path) {
		if (Files.exists(path)) {
			try (InputStream in = Files.newInputStream(path)) {
				load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Writes the property list to the given file path.
	 * 
	 * @param 	path the path to the property file.
	 */
	public void store(Path path) {
		try (OutputStream out = Files.newOutputStream(path)) {
			store(out, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets a value in the property list. The list of values will be converted
	 * to a single string separated by {@value #separator}.
	 * 
	 * @param 	key the key to be placed in this property list.
	 * @param 	values the value corresponding to {@code key}.
	 */
	public void setProperty(String key, List<String> values) {
		setProperty(key, values.stream().collect(Collectors.joining(separator)));
	}
	
	/**
	 * Sets a value in the property list. The {@code Integer} value will be 
	 * stored as a {@code String} value.
	 * 
	 * @param 	key the key to be placed in this property list.
	 * @param 	value the value corresponding to {@code key}.
	 */
	public void setProperty(String key, Integer value) {
		setProperty(key, value == null ? null : value.toString());
	}
	
	/**
	 * Gets a value from the property list as a {@code List}. This method should be used
	 * to retrieve a value previously stored by {@link #setProperty(String, List)}.
	 * 
	 * @param 	key the property key.
	 * @return 	the value in this property list with the specified key value.
	 */
	public List<String> getListProperty(String key) {
		String value = getProperty(key);
		return value == null ? Lists.newLinkedList() : Lists.newLinkedList(Arrays.asList(value.split(separator)));
	}
	
	/**
	 * Gets a value from the property list as an {@code Integer}. This method should be used 
	 * to retrieve a value previously stored by {@link #setProperty(String, Integer)}.
	 * 
	 * @param 	key the property key.
	 * @return 	the value in this property list with the specified key value.
	 */
	public Integer getIntegerProperty(String key) {
		String value = getProperty(key);
		return value != null && !value.isEmpty() ? Integer.parseInt(value) : null;
	}
	
	/**
	 * See {@link #getIntegerProperty(String)}. This method returns {@code defaultValue} when
	 * there is no value in the property list with the specified {@code key}.
	 * 
	 * @param	key the property key.
	 * @param	defaultValue the default value to return when there is no value for the specified key
	 * @return 	the value in this property list with the specified key value or the defaultValue
	 * 			when there is no value with the specified key value.
	 */
	public Integer getIntegerProperty(String key, Integer defaultValue) {
		Integer value = getIntegerProperty(key); 
		return value != null ? value : defaultValue;
	}
	
	/**
	 * This function does the same as {@link #containsKey(Object)}, only for multiple keys.
	 * 
	 * @param 	keys possible keys.
	 * @return 	{@code true} if and only if the specified objects are keys in this hashtable, 
	 * 			as determined by the equals method; false otherwise.
	 */
	public boolean containsKeys(String... keys) {
		return Arrays.asList(keys).stream().allMatch(k -> containsKey(k));
	}
}