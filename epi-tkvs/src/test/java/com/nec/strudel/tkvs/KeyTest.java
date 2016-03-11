/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nec.strudel.tkvs;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nec.strudel.tkvs.Key;

public class KeyTest {

	@Test
	public void intVectorKeyTest() {
		Key key = Key.create(0, 1, 2, 3);

		/**
		 * convert to tuple
		 */
		Object [] idObjs = key.toTuple(Integer.class, Integer.class,
		        Integer.class, Integer.class);
		assertEquals(4, idObjs.length);
		for (int i = 0; i < idObjs.length; i++) {
		    assertTrue(idObjs[i] instanceof Integer);
		    assertEquals(Integer.valueOf(i), idObjs[i]);
		}
		/**
		 * Key -> String -> Key
		 */
		Key key1 = Key.parse(key.toString());
		assertEquals(key, key1);
		assertEquals(key.toStringKey("a"), key1.toStringKey("a"));

		/**
		 * Vector of one element = a simple int key
		 */
		Key key0 = Key.create(0);
		Object[] idObjs0 = key0.toTuple(Integer.class);
		assertEquals(1, idObjs0.length);
		assertEquals(Integer.valueOf(0), idObjs0[0]);
	}

	@Test
	public void testToTuple() {
	    Key key = Key.create("1", 2);
        Object [] inKeys = key.toTuple(Integer.class, Integer.class);
        assertTrue(inKeys[0] instanceof Integer);
        assertEquals(Integer.valueOf(1), inKeys[0]);
        assertTrue(inKeys[1] instanceof Integer);
        Object [] strKeys = key.toTuple(String.class, String.class);
        assertTrue(strKeys[0] instanceof String);
        assertTrue(strKeys[1] instanceof String);
        assertEquals("2", strKeys[1]);
	}
}
