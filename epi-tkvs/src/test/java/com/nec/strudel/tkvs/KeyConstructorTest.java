package com.nec.strudel.tkvs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

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
    public void testPrimitiveSerialize() {
        KeyConstructor integerConst = KeyConstructor.constructorOf(Integer.class);
        testSerDe(integerConst, integerConst.toKey(1));
        KeyConstructor strConst = KeyConstructor.constructorOf(String.class);
        testSerDe(strConst, strConst.toKey("test"));
        KeyConstructor longConst = KeyConstructor.constructorOf(Long.class);
        testSerDe(longConst, longConst.toKey(10L));
    }

    void testSerDe(KeyConstructor cons, Key key) {
        byte[] data = cons.toBytes(key);
        Key key1 = cons.read(data);
        byte[] data1 = cons.toBytes(key1);
        assertEquals(key, key1);
        assertArrayEquals(data, data1);
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
    @Test
    public void testCompoundSerDe() {
        KeyConstructor cons = KeyConstructor.constructorOf(TestKey.class);
        TestKey testKey = new TestKey();
        testKey.setFirst(1);
        testKey.setSecond(2);
        testSerDe(cons, cons.toKey(testKey));
    }

    @Test
    public void testCompoindString() {
        KeyConstructor cons = KeyConstructor.constructorOf(TestStrKey.class);
        TestStrKey testKey = new TestStrKey();
        testKey.setFirst(3);
        testKey.setSecond("1#2");
        Key key = cons.toKey(testKey);
        TestStrKey testKey1 = cons.createKey(key);
        assertEquals(testKey.getFirst(), testKey1.getFirst());
        assertEquals(testKey.getSecond(), testKey1.getSecond());
        assertEquals(key, cons.toKey(testKey1));
        testSerDe(cons, key);
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
    public static class TestStrKey {
        private int first;
        private String second;
        public int getFirst() {
            return first;
        }
        public void setFirst(int first) {
            this.first = first;
        }
        public String getSecond() {
            return second;
        }
        public void setSecond(String second) {
            this.second = second;
        }
    }
}
