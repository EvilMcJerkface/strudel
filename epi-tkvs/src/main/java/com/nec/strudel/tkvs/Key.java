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
     * Gets a raw array without type conversion
     * @return an array of key elements.
     */
    protected abstract Object[] toArray();

    public abstract <T> T convert(Class<T> dstClass);

    public abstract Object[] toTuple(Class<?>... types);

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

    public byte[] toByteKey(String... prefix) {
        StringBuilder sb = new StringBuilder();
        for (String pre : prefix) {
            sb.append(pre).append('_');
        }
        sb.append(key);
        return sb.toString().getBytes();
    }

    public String toStringKey(String...prefix) {
        StringBuilder sb = new StringBuilder();
        for (String pre : prefix) {
            sb.append(pre).append('_');
        }
        sb.append(key);
        return sb.toString();
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

        @Override
        protected Object[] toArray() {
            return new Object[] { key };
        }

        @Override
        public Object[] toTuple(Class<?>... types) {
            if (types.length != 1) {
                throw new TkvStoreException("mismatched key type");
            }
            final Object value = TypeUtil.convertType(key, types[0]);
            return new Object[] {value};
        }

        @Override
        public <T> T convert(Class<T> dstClass) {
            return TypeUtil.convertType(key, dstClass);
        }
    }

    static class CompoundKey extends Key {
        private Object[] keys;

        protected CompoundKey(Object[] keys) {
            super(concat(keys));
            this.keys = keys;
        }

        @Override
        public Object[] toTuple(Class<?>... types) {
            if (keys.length != types.length) {
                throw new TkvStoreException("mismatched key type");
            }
            Object[] values = new Object[types.length];
            for (int i = 0; i < values.length; i++) {
                values[i] = TypeUtil.convertType(keys[i], types[i]);
            }
            return values;
        }

        @Override
        protected Object[] toArray() {
            return keys;
        }

        @Override
        public <T> T convert(Class<T> dstClass) {
            if (keys.length != 1) {
                throw new TkvStoreException("mismatched key type");
            } else {
                return TypeUtil.convertType(keys[0], dstClass);
            }
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
