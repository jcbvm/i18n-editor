package com.jvms.i18neditor.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.jvms.i18neditor.util.TranslationKeys;

public class TranslationKeysTest {
	
	@Test
	public void createTest() {
		assertEquals(TranslationKeys.create("a"), "a");
		assertEquals(TranslationKeys.create("a","b"), "a.b");
		assertEquals(TranslationKeys.create("a","b.c"), "a.b.c");
		assertEquals(TranslationKeys.create(Arrays.asList("a","b","c")), "a.b.c");
		assertEquals(TranslationKeys.create(new String[]{"a","b","c"}), "a.b.c");
	}
	
	@Test
	public void sizeTest() {
		assertEquals(TranslationKeys.size(""), 1);
		assertEquals(TranslationKeys.size("a"), 1);
		assertEquals(TranslationKeys.size("a.b"), 2);
		assertEquals(TranslationKeys.size("a.b.c"), 3);
	}
	
	@Test
	public void partsTest() {
		assertArrayEquals(TranslationKeys.parts(""), new String[]{""});
		assertArrayEquals(TranslationKeys.parts("a"), new String[]{"a"});
		assertArrayEquals(TranslationKeys.parts("a.b"), new String[]{"a","b"});
		assertArrayEquals(TranslationKeys.parts("a.b.c"), new String[]{"a","b","c"});
	}
	
	@Test
	public void subPartsTest() {
		assertArrayEquals(TranslationKeys.subParts("",0), new String[]{""});
		assertArrayEquals(TranslationKeys.subParts("",1), new String[]{});
		assertArrayEquals(TranslationKeys.subParts("a",0), new String[]{"a"});
		assertArrayEquals(TranslationKeys.subParts("a",1), new String[]{});
		assertArrayEquals(TranslationKeys.subParts("a.b",0), new String[]{"a","b"});
		assertArrayEquals(TranslationKeys.subParts("a.b",1), new String[]{"b"});
		assertArrayEquals(TranslationKeys.subParts("a.b",2), new String[]{});
		assertArrayEquals(TranslationKeys.subParts("a.b.c",0), new String[]{"a","b","c"});
		assertArrayEquals(TranslationKeys.subParts("a.b.c",1), new String[]{"b","c"});
		assertArrayEquals(TranslationKeys.subParts("a.b.c",2), new String[]{"c"});
		assertArrayEquals(TranslationKeys.subParts("a.b.c",3), new String[]{});
	}
	
	@Test
	public void firstPartTest() {
		assertEquals(TranslationKeys.firstPart(""), "");
		assertEquals(TranslationKeys.firstPart("a"), "a");
		assertEquals(TranslationKeys.firstPart("a.b"), "a");
		assertEquals(TranslationKeys.firstPart("a.b.c"), "a");
	}
	
	@Test
	public void lastPartTest() {
		assertEquals(TranslationKeys.lastPart(""), "");
		assertEquals(TranslationKeys.lastPart("a"), "a");
		assertEquals(TranslationKeys.lastPart("a.b"), "b");
		assertEquals(TranslationKeys.lastPart("a.b.c"), "c");
	}
	
	@Test
	public void withoutFirstPartTest() {
		assertEquals(TranslationKeys.withoutFirstPart(""), "");
		assertEquals(TranslationKeys.withoutFirstPart("a"), "");
		assertEquals(TranslationKeys.withoutFirstPart("a.b"), "b");
		assertEquals(TranslationKeys.withoutFirstPart("a.b.c"), "b.c");
	}
	
	@Test
	public void withoutLastPartTest() {
		assertEquals(TranslationKeys.withoutLastPart(""), "");
		assertEquals(TranslationKeys.withoutLastPart("a"), "");
		assertEquals(TranslationKeys.withoutLastPart("a.b"), "a");
		assertEquals(TranslationKeys.withoutLastPart("a.b.c"), "a.b");
	}
	
	@Test
	public void isChildKeyOfTest() {
		assertFalse(TranslationKeys.isChildKeyOf("", ""));
		assertFalse(TranslationKeys.isChildKeyOf("a", "a"));
		assertTrue(TranslationKeys.isChildKeyOf("a.b.c", "a"));
		assertTrue(TranslationKeys.isChildKeyOf("a.b.c", "a.b"));
	}
	
	@Test
	public void childKeyTest() {
		assertEquals(TranslationKeys.childKey("", ""), "");
		assertEquals(TranslationKeys.childKey("a", "a"), "");
		assertEquals(TranslationKeys.childKey("a", "a.b.c"), "");
		assertEquals(TranslationKeys.childKey("a.b.c.d", "a.b.c"), "d");
		assertEquals(TranslationKeys.childKey("a.b.c.d.e.f", "a.b.c"), "d.e.f");
		assertEquals(TranslationKeys.childKey("b.c.d", "a.b.c"), "");
	}
	
	@Test
	public void uniqueRootKeysTest() {
		List<String> keys = Lists.newArrayList("a.b.c", "a.b", "b.c.d", "b.c", "c.d.e", "c.d");
		List<String> expected = Lists.newArrayList("a", "b", "c");
		assertEquals(TranslationKeys.uniqueRootKeys(keys), expected);
	}
	
	@Test
	public void extractChildKeysTest() {
		List<String> keys = Lists.newArrayList("a.b.c", "b.c.d", "a.b.c.d.e", "b.c", "b.c.d.e");
		List<String> expected = Lists.newArrayList("d", "d.e");
		assertEquals(TranslationKeys.extractChildKeys(keys, "b.c"), expected);
	}
	
	@Test
	public void isValidTest() {
		assertTrue(TranslationKeys.isValid("a"));
		assertTrue(TranslationKeys.isValid("a.b"));
		assertTrue(TranslationKeys.isValid("a.b.c"));
		assertFalse(TranslationKeys.isValid("."));
		assertFalse(TranslationKeys.isValid(".a"));
		assertFalse(TranslationKeys.isValid(".a.b"));
		assertFalse(TranslationKeys.isValid("a."));
		assertFalse(TranslationKeys.isValid("a.b."));
		assertFalse(TranslationKeys.isValid(" "));
		assertFalse(TranslationKeys.isValid("a .b"));
		assertFalse(TranslationKeys.isValid("a. b"));
	}
}
