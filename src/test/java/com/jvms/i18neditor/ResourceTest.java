package com.jvms.i18neditor;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.SortedMap;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.jvms.i18neditor.Resource;

import static org.junit.Assert.*;

/**
 * 
 * @author Jacob
 */
public class ResourceTest {
	private Resource resource;
	
	@Before
	public void setup() throws Exception {
		LinkedHashMap<String,String>  translations = Maps.newLinkedHashMap();
		translations.put("a.a", "aa");
		translations.put("a.b", "ab");
		resource = new Resource(ResourceType.JSON, null, new Locale("en"));
		resource.setTranslations(translations);
	}
	
	@Test
	public void addTranslationTest() {
		LinkedHashMap<String,String> translations;
		
		resource.storeTranslation("a.a", "b");
		
		translations = resource.getTranslations();
		assertEquals(2, translations.size());
		assertEquals("b", translations.get("a.a"));
		assertEquals("ab", translations.get("a.b"));
		
		resource.storeTranslation("a.a.a", "b");
		
		translations = resource.getTranslations();
		assertEquals(2, translations.size());
		assertEquals("b", translations.get("a.a.a"));
		assertNull(translations.get("a.a"));
		assertEquals("ab", translations.get("a.b"));
		
		resource.storeTranslation("a", "b");
		
		translations = resource.getTranslations();
		assertEquals(1, translations.size());
		assertEquals("b", translations.get("a"));
		assertNull(translations.get("a.a.a"));
		assertNull(translations.get("a.a"));
		assertNull(translations.get("a.b"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void addTranslationWithInvalidKeyTest() {
		resource.storeTranslation("a a", "b");
	}
	
	@Test
	public void removeTranslationTest() {
		LinkedHashMap<String,String> translations;
		
		resource.storeTranslation("b", "b");
		resource.removeTranslation("a");
		
		translations = resource.getTranslations();
		assertEquals(1, translations.size());
		assertEquals("b", translations.get("b"));
		assertNull(translations.get("a.a"));
		assertNull(translations.get("a.b"));
	}
	
	@Test
	public void renameTranslationToUniqueKeyTest() {
		LinkedHashMap<String,String> translations;
		
		resource.storeTranslation("b.a", "ba");
		resource.storeTranslation("b.b", "bb");
		resource.renameTranslation("b", "c");
		
		translations = resource.getTranslations();
		assertEquals(4, translations.size());
		assertEquals("aa", translations.get("a.a"));
		assertEquals("ab", translations.get("a.b"));
		assertNull(translations.get("b.a"));
		assertNull(translations.get("b.b"));
		assertEquals("ba", translations.get("c.a"));
		assertEquals("bb", translations.get("c.b"));
		
		resource.renameTranslation("c.a", "d");
		
		translations = resource.getTranslations();
		assertEquals(4, translations.size());
		assertEquals("ba", translations.get("d"));
		assertEquals("aa", translations.get("a.a"));
		assertEquals("ab", translations.get("a.b"));
		assertNull(translations.get("b.a"));
		assertNull(translations.get("b.b"));
		assertNull(translations.get("c.a"));
		assertEquals("bb", translations.get("c.b"));
	}
	
	@Test
	public void renameTranslationToExistingKeyTest() {
		LinkedHashMap<String,String> translations;
		
		resource.storeTranslation("a.c", "ac");
		resource.storeTranslation("b.a", "ba");
		resource.storeTranslation("b.b.a", "bba");
		resource.storeTranslation("b.b.b", "bbb");
		resource.renameTranslation("b", "a");
		
		translations = resource.getTranslations();
		assertEquals(4, translations.size());
		assertEquals("ba", translations.get("a.a"));
		assertNull(translations.get("a.b"));
		assertEquals("ac", translations.get("a.c"));
		assertEquals("bba", translations.get("a.b.a"));
		assertEquals("bbb", translations.get("a.b.b"));
		assertNull(translations.get("b.a"));
		assertNull(translations.get("b.b.a"));
		assertNull(translations.get("b.b.b"));
		
		resource.renameTranslation("a.b", "a");
		
		translations = resource.getTranslations();
		assertEquals(3, translations.size());
		assertEquals("bba", translations.get("a.a"));
		assertEquals("bbb", translations.get("a.b"));
		assertEquals("ac", translations.get("a.c"));
		assertNull(translations.get("a.b.a"));
		assertNull(translations.get("a.b.b"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void renameTranslationToInvalidKeyTest() {
		resource.storeTranslation("a.b", "ab");
		resource.renameTranslation("a", "b c");
	}
	
	@Test
	public void duplicateTranslationToUniqueKeyTest() {
		LinkedHashMap<String,String> translations;
		
		resource.storeTranslation("b.a", "ba");
		resource.storeTranslation("b.b", "bb");
		resource.duplicateTranslation("b", "c");
		
		translations = resource.getTranslations();
		assertEquals(6, translations.size());
		assertEquals("aa", translations.get("a.a"));
		assertEquals("ab", translations.get("a.b"));
		assertEquals("ba", translations.get("b.a"));
		assertEquals("bb", translations.get("b.b"));
		assertEquals("ba", translations.get("c.a"));
		assertEquals("bb", translations.get("c.b"));
		
		resource.duplicateTranslation("c.a", "d");
		
		translations = resource.getTranslations();
		assertEquals(7, translations.size());
		assertEquals("ba", translations.get("d"));
		assertEquals("aa", translations.get("a.a"));
		assertEquals("ab", translations.get("a.b"));
		assertEquals("ba", translations.get("b.a"));
		assertEquals("bb", translations.get("b.b"));
		assertEquals("ba", translations.get("c.a"));
		assertEquals("bb", translations.get("c.b"));
	}
	
	@Test
	public void duplicateTranslationToExistingKeyTest() {
		LinkedHashMap<String,String> translations;
		
		resource.storeTranslation("a.c", "ac");
		resource.storeTranslation("b.a", "ba");
		resource.storeTranslation("b.b.a", "bba");
		resource.storeTranslation("b.b.b", "bbb");
		resource.duplicateTranslation("b", "a");
		
		translations = resource.getTranslations();
		assertEquals(7, translations.size());
		assertEquals("ba", translations.get("a.a"));
		assertNull(translations.get("a.b"));
		assertEquals("ac", translations.get("a.c"));
		assertEquals("bba", translations.get("a.b.a"));
		assertEquals("bbb", translations.get("a.b.b"));
		assertEquals("ba", translations.get("b.a"));
		assertEquals("bba", translations.get("b.b.a"));
		assertEquals("bbb", translations.get("b.b.b"));
		
		resource.duplicateTranslation("a.b", "a");
		
		translations = resource.getTranslations();
		assertEquals(6, translations.size());
		assertEquals("bba", translations.get("a.a"));
		assertEquals("bbb", translations.get("a.b"));
		assertEquals("ac", translations.get("a.c"));
		assertNull(translations.get("a.b.a"));
		assertNull(translations.get("a.b.b"));
		assertEquals("ba", translations.get("b.a"));
		assertEquals("bba", translations.get("b.b.a"));
		assertEquals("bbb", translations.get("b.b.b"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void duplicateTranslationToInvalidKeyTest() {
		resource.storeTranslation("a.b", "ab");
		resource.duplicateTranslation("a", "b c");
	}
}
