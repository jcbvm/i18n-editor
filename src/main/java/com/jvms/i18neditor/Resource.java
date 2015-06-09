package com.jvms.i18neditor;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jvms.i18neditor.event.ResourceEvent;
import com.jvms.i18neditor.event.ResourceListener;
import com.jvms.i18neditor.util.TranslationKeys;

public class Resource {
	private final Path path;
	private final Locale locale;
	private final SortedMap<String,String> translations;
	private final List<ResourceListener> listeners;
	private final ResourceType type;
	
	public Resource(ResourceType type, Path path, Locale locale) {
		this(type, path, locale, Maps.newTreeMap());
	}
	
	public Resource(ResourceType type, Path path, Locale locale, SortedMap<String,String> translations) {
		this.path = path;
		this.translations = translations;
		this.locale = locale;
		this.listeners = Lists.newLinkedList();
		this.type = type;
	}
	
	public ResourceType getType() {
		return type;
	}
	
	public Path getPath() {
		return path;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public SortedMap<String,String> getTranslations() {
		return ImmutableSortedMap.copyOf(translations);
	}
	
	public void storeTranslation(String key, String value) {
		String existing = translations.get(key);
		if (existing != null && existing.equals(value)) return;
		if (existing == null && value.isEmpty()) return;
		removeParents(key);
		removeChildren(key);
		if (value.isEmpty()) {
			translations.remove(key);
		} else {
			translations.put(key, value);
		}
		notifyListeners(key);
	}
	
	public void removeTranslation(String key) {
		removeChildren(key);
		if (translations.containsKey(key)) {
			translations.remove(key);
			notifyListeners(key);
		}
	}
	
	public void renameTranslation(String key, String newName) {
		String newKey = TranslationKeys.create(TranslationKeys.withoutLastPart(key), newName);
		removeTranslation(newKey);
		Lists.newArrayList(translations.keySet()).forEach(k -> {
			if (TranslationKeys.isChildKeyOf(k, key)) {
				String nk = TranslationKeys.create(newKey, TranslationKeys.childKey(k, key));
				translations.put(nk, translations.get(k));
				notifyListeners(nk);
			}
		});
		if (translations.containsKey(key)) {
			translations.put(newKey, translations.get(key));
			notifyListeners(newKey);
		}
		removeTranslation(key);
	}
	
	public void addListener(ResourceListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ResourceListener listener) {
		listeners.remove(listener);
	}
	
	private void removeChildren(String key) {
		Lists.newArrayList(translations.keySet()).forEach(k -> {
			if (TranslationKeys.isChildKeyOf(k, key)) {
				translations.remove(k);
				notifyListeners(k);
			}
		});
	}
	
	private void removeParents(String key) {
		Lists.newArrayList(translations.keySet()).forEach(k -> {
			if (TranslationKeys.isChildKeyOf(key, k)) {
				translations.remove(k);
				notifyListeners(k);
			}
		});
	}
	
	private void notifyListeners(String key) {
		listeners.forEach(l -> l.resourceChanged(new ResourceEvent(this, key)));
	}
	
	public enum ResourceType {
		ES6, JSON
	}
}
