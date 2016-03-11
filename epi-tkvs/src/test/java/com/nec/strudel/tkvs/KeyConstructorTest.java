package com.nec.strudel.tkvs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KeyConstructorTest {

    @Test
    public void testPrimitive() {
        KeyConstructor integerConst = KeyConstructor.constructorOf(Integer.class);
        KeyConstructor intConst = KeyConstructor.constructorOf(int.class);
        Key key = integerConst.toKey(1);
        Key key2 = intConst.toKey(2);
        
        assertEquals(Integer.valueOf(1), integerConst.createKey(key));
        assertEquals(Integer.valueOf(2), intConst.createKey(key2));

        assertEquals(Integer.valueOf(1), key.convert(Integer.class));
        assertEquals(key, integerConst.toKey(integerConst.createKey(key)));
        assertEquals(integerConst.toKey(3), intConst.toKey(3));
    }

    @Test
    public void testCompound() {
        KeyConstructor cons = KeyConstructor.constructorOf(TestKey.class);
        TestKey testKey = new TestKey();
        testKey.setFirst(1);
        testKey.setSecond(2);
        Key key = cons.toKey(testKey);
        TestKey testKey1 = cons.createKey(key);
        assertEquals(testKey.getFirst(), testKey1.getFirst());
        assertEquals(testKey.getSecond(), testKey1.getSecond());
        assertEquals(key, cons.toKey(testKey1));
    }

    public static class TestKey {
        private int first;
        private int second;
        public int getFirst() {
            return first;
        }
        public void setFirst(int first) {
            this.first = first;
        }
        public int getSecond() {
            return second;
        }
        public void setSecond(int second) {
            this.second = second;
        }
    }
}
