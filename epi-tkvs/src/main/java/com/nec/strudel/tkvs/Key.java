/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
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

/**
 * The key of a key-value pair. It is also used to represent the key of an
 * entity group. In the current version, a key is just a string.
 * 
 * @author tatemura
 *
 */
public abstract class Key {
    private static final String INT_DELIM = "#";
    private final String key;

    public static Key parse(byte[] image) {
        String key = new String(image);
        String[] ids = key.toString().split(INT_DELIM);
        if (ids.length > 1) {
            return new CompoundKey(ids);
        } else {
            return new SingleKey(key);
        }
    }

    public static Key parse(String key) {
        String[] ids = key.split(INT_DELIM);
        if (ids.length > 1) {
            return new CompoundKey(ids);
        } else {
            return new SingleKey(key);
        }
    }

    public static Key create(Object... ids) {
        if (ids.length == 1) {
            return new SingleKey(ids[0]);
        }
        return new CompoundKey(ids);
    }

    protected Key(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }

    /**
     * @throws NumberFormatException
     * @return the value when the key is interpreted as an integer.
     */
    public abstract int toInt();

    public abstract int[] toIntArray();

    public abstract Object[] toArray();

    public abstract Record toRecord();

    /**
     * TODO implement better
     */
    public <T> T convert(Class<T> dstClass) {
        return Record.convert(key.toString(), dstClass);
    }

    public Object[] toTuple(Class<?>[] types) {
        return toRecord().toTuple(types);
    }

    public Key concat(Key key) {
        Object[] vec1 = this.toArray();
        Object[] vec2 = key.toArray();
        Object[] vec = new Object[vec1.length + vec2.length];
        for (int i = 0; i < vec1.length; i++) {
            vec[i] = vec1[i];
        }
        for (int i = 0; i < vec2.length; i++) {
            vec[i + vec1.length] = vec2[i];
        }
        return new CompoundKey(vec);
    }

    public byte[] toBytes() {
        return key.getBytes();
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Key) {
            Key keyObj = (Key) obj;
            return key.equals(keyObj.key);
        }
        return false;
    }

    static class SingleKey extends Key {
        private Object key;

        protected SingleKey(Object key) {
            super(key.toString());
            this.key = key;
        }

        public int toInt() {
            if (key instanceof Integer) {
                return (Integer) key;
            } else {
                return Integer.parseInt(
                        key.toString());
            }
        }

        @Override
        public int[] toIntArray() {
            return new int[] { toInt() };
        }

        @Override
        public Object[] toArray() {
            return new Object[] { key };
        }

        public Record toRecord() {
            return Record.create(key);
        }

        @Override
        public <T> T convert(Class<T> dstClass) {
            if (dstClass.isInstance(key)) {
                return dstClass.cast(key);
            }
            return super.convert(dstClass);
        }
    }

    static class CompoundKey extends Key {
        private Object[] keys;

        protected CompoundKey(Object[] keys) {
            super(concat(keys));
            this.keys = keys;
        }

        public Record toRecord() {
            return Record.create(keys);
        }

        @Override
        public int toInt() {
            throw new NumberFormatException(
                    "compound key cannot be converted to an integer");
        }

        public int[] toIntArray() {
            int[] keyvec = new int[keys.length];
            for (int i = 0; i < keys.length; i++) {
                if (keys[i] instanceof Integer) {
                    keyvec[i] = (Integer) keys[i];
                } else {
                    keyvec[i] = Integer.parseInt(
                            keys[i].toString());
                }
            }
            return keyvec;
        }

        @Override
        public Object[] toArray() {
            // TODO copy it for safety?
            return keys;
        }

        static String concat(Object[] ids) {
            StringBuilder sb = new StringBuilder();
            sb.append(ids[0].toString());
            for (int i = 1; i < ids.length; i++) {
                sb.append(INT_DELIM)
                        .append(ids[i].toString());
            }
            return sb.toString();
        }
    }
}
