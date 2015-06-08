package com.jvms.i18neditor;

import java.util.Locale;
import java.util.SortedMap;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.jvms.i18neditor.Resource;
import com.jvms.i18neditor.Resource.ResourceType;

import static org.junit.Assert.*;

public class ResourceTest {
	private Resource resource;
	
	@Before
	public void setup() throws Exception {
		SortedMap<String,String> translations = Maps.newTreeMap();
		translations.put("a.a", "aa");
		translations.put("a.b", "ab");
		resource = new Resource(ResourceType.JSON, null, new Locale("en"), translations);
	}
	
	@Test
	public void addTranslationTest() {
		SortedMap<String,String> translations;
		
		resource.addTranslation("a.a", "b");
		
		translations = resource.getTranslations();
		assertEquals(translations.size(), 2);
		assertEquals(translations.get("a.a"), "b");
		assertEquals(translations.get("a.b"), "ab");
		
		resource.addTranslation("a.a.a", "b");
		
		translations = resource.getTranslations();
		assertEquals(translations.size(), 2);
		assertEquals(translations.get("a.a.a"), "b");
		assertEquals(translations.get("a.a"), null);
		assertEquals(translations.get("a.b"), "ab");
		
		resource.addTranslation("a", "b");
		
		translations = resource.getTranslations();
		assertEquals(translations.size(), 1);
		assertEquals(translations.get("a"), "b");
		assertEquals(translations.get("a.a.a"), null);
		assertEquals(translations.get("a.a"), null);
		assertEquals(translations.get("a.b"), null);
	}
	
	@Test
	public void removeTranslationTest() {
		SortedMap<String,String> translations;
		
		resource.addTranslation("b", "b");
		resource.removeTranslation("a");
		
		translations = resource.getTranslations();
		assertEquals(translations.size(), 1);
		assertEquals(translations.get("b"), "b");
		assertEquals(translations.get("a.a"), null);
		assertEquals(translations.get("a.b"), null);
	}
	
	@Test
	public void renameTranslationToUniqueKeyTest() {
		SortedMap<String,String> translations;
		
		resource.addTranslation("b.a", "ba");
		resource.addTranslation("b.b", "bb");
		resource.renameTranslation("b", "c");
		
		translations = resource.getTranslations();
		assertEquals(translations.size(), 4);
		assertEquals(translations.get("a.a"), "aa");
		assertEquals(translations.get("a.b"), "ab");
		assertEquals(translations.get("b.a"), null);
		assertEquals(translations.get("b.b"), null);
		assertEquals(translations.get("c.a"), "ba");
		assertEquals(translations.get("c.b"), "bb");
	}
	
	@Test
	public void renameTranslationToExistingKeyTest() {
		SortedMap<String,String> translations;
		
		resource.addTranslation("a.c", "ac");
		resource.addTranslation("b.a", "ba");
		resource.addTranslation("b.b.a", "bba");
		resource.addTranslation("b.b.b", "bbb");
		resource.renameTranslation("b", "a");
		
		translations = resource.getTranslations();
		assertEquals(translations.size(), 3);
		assertEquals(translations.get("a.c"), null);
		assertEquals(translations.get("a.a"), "ba");
		assertEquals(translations.get("a.b.a"), "bba");
		assertEquals(translations.get("a.b.b"), "bbb");
		assertEquals(translations.get("b.a"), null);
		assertEquals(translations.get("b.b.a"), null);
		assertEquals(translations.get("b.b.b"), null);
	}
}
