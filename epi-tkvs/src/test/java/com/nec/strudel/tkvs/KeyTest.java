package com.nec.strudel.tkvs;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.nec.strudel.tkvs.Key;

public class KeyTest {

	@Test
	public void intVectorKeyTest() {
		Key key = Key.create(0, 1, 2, 3);
		int[] ids = key.toIntArray();
		assertTrue(Arrays.equals(new int[]{0, 1, 2, 3}, ids));
		/**
		 * Key -> String -> Key
		 */
		Key key1 = Key.parse(key.toString());
		assertEquals(key, key1);
		assertTrue(Arrays.equals(new int[]{0, 1, 2, 3}, key1.toIntArray()));
		/**
		 * Vector of one element = a simple int key
		 */
		Key key0 = Key.create(0);
		assertTrue(Arrays.equals(new int[]{0}, key0.toIntArray()));
		assertEquals(0, key0.toInt());
	}
}
