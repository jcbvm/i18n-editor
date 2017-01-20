package com.jvms.i18neditor.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class ResourceKeysTest {
	
	@Test
	public void createTest() {
		assertEquals(ResourceKeys.create("a"), "a");
		assertEquals(ResourceKeys.create("a","b"), "a.b");
		assertEquals(ResourceKeys.create("a","b.c"), "a.b.c");
		assertEquals(ResourceKeys.create(Arrays.asList("a","b","c")), "a.b.c");
		assertEquals(ResourceKeys.create(new String[]{"a","b","c"}), "a.b.c");
	}
	
	@Test
	public void sizeTest() {
		assertEquals(ResourceKeys.size(""), 1);
		assertEquals(ResourceKeys.size("a"), 1);
		assertEquals(ResourceKeys.size("a.b"), 2);
		assertEquals(ResourceKeys.size("a.b.c"), 3);
	}
	
	@Test
	public void partsTest() {
		assertArrayEquals(ResourceKeys.parts(""), new String[]{""});
		assertArrayEquals(ResourceKeys.parts("a"), new String[]{"a"});
		assertArrayEquals(ResourceKeys.parts("a.b"), new String[]{"a","b"});
		assertArrayEquals(ResourceKeys.parts("a.b.c"), new String[]{"a","b","c"});
	}
	
	@Test
	public void subPartsTest() {
		assertArrayEquals(ResourceKeys.subParts("",0), new String[]{""});
		assertArrayEquals(ResourceKeys.subParts("",1), new String[]{});
		assertArrayEquals(ResourceKeys.subParts("a",0), new String[]{"a"});
		assertArrayEquals(ResourceKeys.subParts("a",1), new String[]{});
		assertArrayEquals(ResourceKeys.subParts("a.b",0), new String[]{"a","b"});
		assertArrayEquals(ResourceKeys.subParts("a.b",1), new String[]{"b"});
		assertArrayEquals(ResourceKeys.subParts("a.b",2), new String[]{});
		assertArrayEquals(ResourceKeys.subParts("a.b.c",0), new String[]{"a","b","c"});
		assertArrayEquals(ResourceKeys.subParts("a.b.c",1), new String[]{"b","c"});
		assertArrayEquals(ResourceKeys.subParts("a.b.c",2), new String[]{"c"});
		assertArrayEquals(ResourceKeys.subParts("a.b.c",3), new String[]{});
	}
	
	@Test
	public void firstPartTest() {
		assertEquals(ResourceKeys.firstPart(""), "");
		assertEquals(ResourceKeys.firstPart("a"), "a");
		assertEquals(ResourceKeys.firstPart("a.b"), "a");
		assertEquals(ResourceKeys.firstPart("a.b.c"), "a");
	}
	
	@Test
	public void lastPartTest() {
		assertEquals(ResourceKeys.lastPart(""), "");
		assertEquals(ResourceKeys.lastPart("a"), "a");
		assertEquals(ResourceKeys.lastPart("a.b"), "b");
		assertEquals(ResourceKeys.lastPart("a.b.c"), "c");
	}
	
	@Test
	public void withoutFirstPartTest() {
		assertEquals(ResourceKeys.withoutFirstPart(""), "");
		assertEquals(ResourceKeys.withoutFirstPart("a"), "");
		assertEquals(ResourceKeys.withoutFirstPart("a.b"), "b");
		assertEquals(ResourceKeys.withoutFirstPart("a.b.c"), "b.c");
	}
	
	@Test
	public void withoutLastPartTest() {
		assertEquals(ResourceKeys.withoutLastPart(""), "");
		assertEquals(ResourceKeys.withoutLastPart("a"), "");
		assertEquals(ResourceKeys.withoutLastPart("a.b"), "a");
		assertEquals(ResourceKeys.withoutLastPart("a.b.c"), "a.b");
	}
	
	@Test
	public void isChildKeyOfTest() {
		assertFalse(ResourceKeys.isChildKeyOf("", ""));
		assertFalse(ResourceKeys.isChildKeyOf("a", "a"));
		assertTrue(ResourceKeys.isChildKeyOf("a.b.c", "a"));
		assertTrue(ResourceKeys.isChildKeyOf("a.b.c", "a.b"));
	}
	
	@Test
	public void childKeyTest() {
		assertEquals(ResourceKeys.childKey("", ""), "");
		assertEquals(ResourceKeys.childKey("a", "a"), "");
		assertEquals(ResourceKeys.childKey("a", "a.b.c"), "");
		assertEquals(ResourceKeys.childKey("a.b.c.d", "a.b.c"), "d");
		assertEquals(ResourceKeys.childKey("a.b.c.d.e.f", "a.b.c"), "d.e.f");
		assertEquals(ResourceKeys.childKey("b.c.d", "a.b.c"), "");
	}
	
	@Test
	public void uniqueRootKeysTest() {
		List<String> keys = Lists.newArrayList("a.b.c", "a.b", "b.c.d", "b.c", "c.d.e", "c.d");
		List<String> expected = Lists.newArrayList("a", "b", "c");
		assertEquals(ResourceKeys.uniqueRootKeys(keys), expected);
	}
	
	@Test
	public void extractChildKeysTest() {
		List<String> keys = Lists.newArrayList("a.b.c", "b.c.d", "a.b.c.d.e", "b.c", "b.c.d.e");
		List<String> expected = Lists.newArrayList("d", "d.e");
		assertEquals(ResourceKeys.extractChildKeys(keys, "b.c"), expected);
	}
	
	@Test
	public void isValidTest() {
		assertTrue(ResourceKeys.isValid("a"));
		assertTrue(ResourceKeys.isValid("a.b"));
		assertTrue(ResourceKeys.isValid("a.b.c"));
		assertFalse(ResourceKeys.isValid("."));
		assertFalse(ResourceKeys.isValid(".a"));
		assertFalse(ResourceKeys.isValid(".a.b"));
		assertFalse(ResourceKeys.isValid("a."));
		assertFalse(ResourceKeys.isValid("a.b."));
		assertFalse(ResourceKeys.isValid(" "));
		assertFalse(ResourceKeys.isValid("a.\tb"));
		assertFalse(ResourceKeys.isValid("a.\nb"));
		assertFalse(ResourceKeys.isValid("a .b"));
		assertFalse(ResourceKeys.isValid("a. b"));
	}
}
