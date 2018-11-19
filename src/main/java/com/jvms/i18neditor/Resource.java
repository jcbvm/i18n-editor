package com.jvms.i18neditor;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jvms.i18neditor.util.ResourceKeys;

/**
 * A resource is a container for storing i18n data and is defined by the following properties:
 * 
 * <ul>
 * <li>{@code type} the type of the resource, either {@code JSON} or {@code ES6}.</li>
 * <li>{@code path}	the path to the resource file on disk.</li>
 * <li>{@code locale} the locale of the resource.</li>
 * <li>{@code translations} a sorted map containing the translations of the resource by key value pair.</li>
 * </ul>
 * 
 * <p>Objects can listen to a resource by adding a {@link ResourceListener} which 
 * will be called when any change is made to the {@code translations}.</p>
 * 
 * @author Jacob van Mourik
 */
public class Resource {
	private final Path path;
	private final Locale locale;
	private final ResourceType type;
	private final List<ResourceListener> listeners = Lists.newLinkedList();
	private LinkedHashMap<String,String> translations = Maps.newLinkedHashMap();
	private String checksum;

	/**
	 * See {@link #Resource(ResourceType, Path, Locale)}.
	 */
	public Resource(ResourceType type, Path path) {
		this(type, path, null);
	}
	
	/**
	 * Creates a new instance of a resource.
	 * 
	 * @param 	type the type of the resource.
	 * @param 	path the path to the file on disk.
	 * @param 	locale the locale of the translations.
	 */
	public Resource(ResourceType type, Path path, Locale locale) {
		this.path = path;
		this.locale = locale;
		this.type = type;
	}
	
	/**
	 * Gets the type of the resource.
	 * 
	 * @return 	the type.
	 */
	public ResourceType getType() {
		return type;
	}
	
	/**
	 * Gets the path to the resource file on disk.
	 * 
	 * @return 	the path.
	 */
	public Path getPath() {
		return path;
	}
	
	/**
	 * Gets the locale of the translations of the resource.
	 * 
	 * @return 	the locale of the resource, may be {@code null}.
	 */
	public Locale getLocale() {
		return locale;
	}
	
	/**
	 * Gets a map of the translations of the resource.
	 * 
	 * <p>The returned map is an immutable sorted map. Modifications to the translations should be done via 
	 * {@link #storeTranslation(String, String)}, {@link #removeTranslation(String)} or 
	 * {@link #renameTranslation(String, String)}.</p>
	 * 
	 * @return 	the translations of the resource.
	 */
	public LinkedHashMap<String,String> getTranslations() {
		return translations;
	}
	
	/**
	 * Sets the translations of the resource.
	 * 
	 * @param translations	the translations
	 */
	public void setTranslations(LinkedHashMap<String,String> translations) {
		this.translations = translations;
	}
	
	/**
	 * Checks whether the resource has a translation with the given key.
	 * 
	 * @param	key the key of the translation to look for.
	 * @return 	whether a translation with the given key exists.
	 */
	public boolean hasTranslation(String key) {
		return !Strings.isNullOrEmpty(translations.get(key));
	}
	
	/**
	 * Gets a translation from the resource's translations.
	 * 
	 * @param	key the key of the translation to get.
	 * @return 	value of the translation or {@code null} if there is no translation for the given key.
	 */
	public String getTranslation(String key) {
		return translations.get(key);
	}
	
	/**
	 * Stores a translation to the resource's translations.
	 * 
	 * <ul>
	 * <li>If the given key does not exists yet and is not empty, a new translation will be added to the map.</li>
	 * <li>If the given key already exists, the existing value will be overwritten with the given value.</li>
	 * <li>If the given key is a parent key of any existing keys, the existing child keys will be removed (when parent values are not supported).</li>
	 * <li>If the given key is a child key of any existing keys, the existing parent keys will be removed (when parent values are not supported).</li>
	 * </ul>
	 * 
	 * @param 	key the key of the translation to add.
	 * @param 	value the value of the translation to add corresponding the given key.
	 */
	public void storeTranslation(String key, String value) {
		checkKey(key);
		String existing = translations.get(key);
		if (value == null || existing != null && existing.equals(value)) {
			return;
		}
		if (!supportsParentValues()) {
			removeParents(key);
			removeChildren(key);
		}
		translations.put(key, value);
		notifyListeners();
	}
	
	/**
	 * Removes a translation from the resource's translations. 
	 * Any child keys of the given key will also be removed.
	 * 
	 * @param 	key the key of the translation to remove.
	 */
	public void removeTranslation(String key) {
		removeChildren(key);
		translations.remove(key);
		notifyListeners();
	}
	
	/**
	 * Renames a translation key in the resource's translations.
	 * Any existing child keys of the translation key will also be renamed.
	 * 
	 * @param 	key the old key of the translation to rename.
	 * @param 	newKey the new key.
	 */
	public void renameTranslation(String key, String newKey) {
		checkKey(newKey);
		duplicateTranslation(key, newKey, false);
		notifyListeners();
	}
	
	/**
	 * Duplicates a translation key, and any child keys, in the resource's translations.
	 * 
	 * @param 	key the key of the translation to duplicate.
	 * @param 	newKey the new key.
	 */
	public void duplicateTranslation(String key, String newKey) {
		checkKey(newKey);
		duplicateTranslation(key, newKey, true);
		notifyListeners();
	}
	
	/**
	 * Adds a listener to the resource. The listener will be called whenever there is made 
	 * a change to the translations of the resource.
	 * 
	 * @param 	listener the listener to add.
	 */
	public void addListener(ResourceListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes a listener from the resource previously added by {@link #addListener(ResourceListener)}.
	 * 
	 * @param 	listener the listener to remove.
	 */
	public void removeListener(ResourceListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Gets the checksum of the resource's file.
	 * This method only returns the checksum set via {@link #setChecksum(checksum)}.
	 * 
	 * @return	the checksum.
	 */
	public String getChecksum() {
		return checksum;
	}
	
	/**
	 * Returns whether the resource has support for parent values.
	 * 
	 * <p>For example if we have a value set for the key <code>a.b</code> we 
	 * might also set a value for <code>a</code>.</p>
	 * 
	 * @return 	whether parent values are supported.
	 */
	public boolean supportsParentValues() {
		return type == ResourceType.Properties;
	}
	
	/**
	 * Sets the checksum of the resource's file.
	 * 
	 * @param checksum	the checksum to set.
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	private void duplicateTranslation(String key, String newKey, boolean keepOld) {
		Map<String,String> newTranslations = Maps.newTreeMap();
		translations.keySet().forEach(k -> {
			if (ResourceKeys.isChildKeyOf(k, key)) {
				String nk = ResourceKeys.create(newKey, ResourceKeys.childKey(k, key));
				newTranslations.put(nk, translations.get(k));
			}
		});
		if (translations.containsKey(key)) {
			newTranslations.put(newKey, translations.get(key));
		}
		if (!keepOld) {
			removeChildren(key);
			translations.remove(key);
		}
		newTranslations.forEach(this::storeTranslation);
	}
	
	private void removeChildren(String key) {
		Lists.newLinkedList(translations.keySet()).forEach(k -> {
			if (ResourceKeys.isChildKeyOf(k, key)) {
				translations.remove(k);
			}
		});
	}
	
	private void removeParents(String key) {
		Lists.newLinkedList(translations.keySet()).forEach(k -> {
			if (ResourceKeys.isChildKeyOf(key, k)) {
				translations.remove(k);
			}
		});
	}
	
	private void notifyListeners() {
		listeners.forEach(l -> l.resourceChanged(new ResourceEvent(this)));
	}
	
	private void checkKey(String key) {
		Preconditions.checkArgument(ResourceKeys.isValid(key), "Key is not valid.");
	}
}
