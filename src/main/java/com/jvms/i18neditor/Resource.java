package com.jvms.i18neditor;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jvms.i18neditor.util.TranslationKeys;

/**
 * A resource is a container for storing translation data and is defined by the following properties:
 * 
 * <ul>
 * <li>{@code type} the type of the resource, either {@code JSON} or {@code ES6}.</li>
 * <li>{@code path}	the path to the translation file on disk.</li>
 * <li>{@code locale} the locale of the translations.</li>
 * <li>{@code translations} a sorted map containing the translations.</li>
 * </ul>
 * 
 * <p>Objects can listen to a resource by adding a {@link ResourceListener} which 
 * will be called when any change is made to the {@code translations}.</p>
 * 
 * @author Jacob
 */
public class Resource {
	private final Path path;
	private final Locale locale;
	private final SortedMap<String,String> translations;
	private final List<ResourceListener> listeners = Lists.newLinkedList();
	private final ResourceType type;
	
	/**
	 * An enum for defining the type of a resource.
	 */
	public enum ResourceType {
		JSON, ES6
	}
	
	/**
	 * See {@link #Resource(ResourceType, Path, Locale, SortedMap)}.
	 */
	public Resource(ResourceType type, Path path, Locale locale) {
		this(type, path, locale, Maps.newTreeMap());
	}
	
	/**
	 * Creates a new instance of a resource.
	 * 
	 * @param 	type the type of the resource.
	 * @param 	path the path to the file on disk.
	 * @param 	locale the locale of the translations.
	 * @param 	translations the actual translation data.
	 */
	public Resource(ResourceType type, Path path, Locale locale, SortedMap<String,String> translations) {
		this.path = path;
		this.translations = translations;
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
	 * @return 	the locale of the resource.
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
	public SortedMap<String,String> getTranslations() {
		return ImmutableSortedMap.copyOf(translations);
	}
	
	/**
	 * Stores a translation to the resource's translations.
	 * 
	 * <ul>
	 * <li>If the given key does not exists yet, a new translation will be added to the map.</li>
	 * <li>If the given key already exists, the existing value will be overwritten with the given value.</li>
	 * <li>If the given key is a parent key of any existing keys, the existing child keys will be removed.</li>
	 * <li>If the given key is a child key of any existing keys, the existing parent keys will be removed.</li>
	 * </ul>
	 * 
	 * @param 	key the key of the translation to add.
	 * @param 	value the value of the translation to add corresponding the given key.
	 */
	public void storeTranslation(String key, String value) {
		String existing = translations.get(key);
		if (existing != null && existing.equals(value)) return;
		//if (existing == null && value.isEmpty()) return;
		removeParents(key);
		removeChildren(key);
		if (value.isEmpty()) {
			translations.remove(key);
		} else {
			translations.put(key, value);
		}
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
	
	private void duplicateTranslation(String key, String newKey, boolean keepOld) {
		Map<String,String> newTranslations = Maps.newTreeMap();
		translations.keySet().forEach(k -> {
			if (TranslationKeys.isChildKeyOf(k, key)) {
				String nk = TranslationKeys.create(newKey, TranslationKeys.childKey(k, key));
				newTranslations.put(nk, translations.get(k));
			}
		});
		if (translations.containsKey(key)) {
			newTranslations.put(newKey, translations.get(key));
		}
		removeChildren(newKey);
		translations.remove(newKey);
		if (!keepOld) {
			removeChildren(key);
			translations.remove(key);
		}
		translations.putAll(newTranslations);
	}
	
	private void removeChildren(String key) {
		Lists.newLinkedList(translations.keySet()).forEach(k -> {
			if (TranslationKeys.isChildKeyOf(k, key)) {
				translations.remove(k);
			}
		});
	}
	
	private void removeParents(String key) {
		Lists.newLinkedList(translations.keySet()).forEach(k -> {
			if (TranslationKeys.isChildKeyOf(key, k)) {
				translations.remove(k);
			}
		});
	}
	
	private void notifyListeners() {
		listeners.forEach(l -> l.resourceChanged(new ResourceEvent(this)));
	}
}
