package com.jvms.i18neditor.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * 
 * @author Jacob
 */
public class ResourceKeysTest {
	
	@Test
	public void createTest() {
		assertEquals("a", ResourceKeys.create("a"));
		assertEquals("a.b", ResourceKeys.create("a","b"));
		assertEquals("a.b.c", ResourceKeys.create("a","b.c"));
		assertEquals("a.b.c", ResourceKeys.create(Arrays.asList("a","b","c")));
		assertEquals("a.b.c", ResourceKeys.create(new String[]{"a","b","c"}));
	}
	
	@Test
	public void sizeTest() {
		assertEquals(1, ResourceKeys.size(""));
		assertEquals(1, ResourceKeys.size("a"));
		assertEquals(2, ResourceKeys.size("a.b"));
		assertEquals(3, ResourceKeys.size("a.b.c"));
	}
	
	@Test
	public void partsTest() {
		assertArrayEquals(new String[]{""}, ResourceKeys.parts(""));
		assertArrayEquals(new String[]{"a"}, ResourceKeys.parts("a"));
		assertArrayEquals(new String[]{"a.b"}, ResourceKeys.parts("a.b"));
		assertArrayEquals(new String[]{"a.b.c"}, ResourceKeys.parts("a.b.c"));
	}
	
	@Test
	public void subPartsTest() {
		assertArrayEquals(new String[]{""}, ResourceKeys.subParts("",0));
		assertArrayEquals(new String[]{}, ResourceKeys.subParts("",1));
		assertArrayEquals(new String[]{"a"}, ResourceKeys.subParts("a",0));
		assertArrayEquals(new String[]{}, ResourceKeys.subParts("a",1));
		assertArrayEquals(new String[]{"a.b"}, ResourceKeys.subParts("a.b",0));
		assertArrayEquals(new String[]{"a.b"}, ResourceKeys.subParts("a.b",1));
		assertArrayEquals(new String[]{"a.b"}, ResourceKeys.subParts("a.b",2));
		assertArrayEquals(new String[]{"a","b","c"}, ResourceKeys.subParts("a.b.c",0));
		assertArrayEquals(new String[]{"b","c"}, ResourceKeys.subParts("a.b.c",1));
		assertArrayEquals(new String[]{"c"}, ResourceKeys.subParts("a.b.c",2));
		assertArrayEquals(new String[]{}, ResourceKeys.subParts("a.b.c",3));
	}
	
	@Test
	public void firstPartTest() {
		assertEquals("", ResourceKeys.firstPart(""));
		assertEquals("a", ResourceKeys.firstPart("a"));
		assertEquals("a", ResourceKeys.firstPart("a.b"));
		assertEquals("a", ResourceKeys.firstPart("a.b.c"));
	}
	
	@Test
	public void lastPartTest() {
		assertEquals("", ResourceKeys.lastPart(""));
		assertEquals("a", ResourceKeys.lastPart("a"));
		assertEquals("b", ResourceKeys.lastPart("a.b"));
		assertEquals("c", ResourceKeys.lastPart("a.b.c"));
	}
	
	@Test
	public void withoutFirstPartTest() {
		assertEquals("", ResourceKeys.withoutFirstPart(""));
		assertEquals("", ResourceKeys.withoutFirstPart("a"));
		assertEquals("b", ResourceKeys.withoutFirstPart("a.b"));
		assertEquals("b.c", ResourceKeys.withoutFirstPart("a.b.c"));
	}
	
	@Test
	public void withoutLastPartTest() {
		assertEquals("", ResourceKeys.withoutLastPart(""));
		assertEquals("", ResourceKeys.withoutLastPart("a"));
		assertEquals("a", ResourceKeys.withoutLastPart("a.b"));
		assertEquals("a.b", ResourceKeys.withoutLastPart("a.b.c"));
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
		assertEquals("", ResourceKeys.childKey("", ""));
		assertEquals("", ResourceKeys.childKey("a", "a"));
		assertEquals("", ResourceKeys.childKey("a", "a.b.c"));
		assertEquals("d", ResourceKeys.childKey("a.b.c.d", "a.b.c"));
		assertEquals("d.e.f", ResourceKeys.childKey("a.b.c.d.e.f", "a.b.c"));
		assertEquals("", ResourceKeys.childKey("b.c.d", "a.b.c"));
	}
	
	@Test
	public void uniqueRootKeysTest() {
		List<String> keys = Lists.newArrayList("a.b.c", "a.b", "b.c.d", "b.c", "c.d.e", "c.d");
		List<String> expected = Lists.newArrayList("a", "b", "c");
		assertEquals(expected, ResourceKeys.uniqueRootKeys(keys));
	}
	
	@Test
	public void extractChildKeysTest() {
		List<String> keys = Lists.newArrayList("a.b.c", "b.c.d", "a.b.c.d.e", "b.c", "b.c.d.e");
		List<String> expected = Lists.newArrayList("d", "d.e");
		assertEquals(expected, ResourceKeys.extractChildKeys(keys, "b.c"));
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
