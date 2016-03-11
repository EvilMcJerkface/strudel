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

import java.util.List;

import com.nec.strudel.entity.info.BeanInfo;
import com.nec.strudel.entity.info.ValueInfo;
import com.nec.strudel.entity.info.ValueTypes;

public abstract class KeyConstructor {

    public abstract <T> T createKey(Key key);

    public abstract Key toKey(Object key);

    public abstract byte[] toBytes(Key key);

    public abstract Key read(byte[] data);

    public static KeyConstructor constructorOf(Class<?> keyClass) {
        if (ValueTypes.isPrimitive(keyClass)) {
            return new PrimitiveKeyConstructor(keyClass);
        } else {
            return new BeanKeyConstructor(new BeanInfo(keyClass));
        }
    }

    public static KeyConstructor constructorOf(ValueInfo info) {
        if (info.isPrimitive()) {
            return new PrimitiveKeyConstructor(info.valueClass());
        } else {
            return new BeanKeyConstructor((BeanInfo) info);
        }
    }

    static class BeanKeyConstructor extends KeyConstructor {
        private final BeanInfo info;
        private final Class<?>[] types;
        private final RecordFormat format;

        public BeanKeyConstructor(BeanInfo info) {
            this.info = info;
            List<Class<?>> list = info.types();
            types = list.toArray(new Class<?>[list.size()]);
            this.format = new ArrayRecordFormat(types);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T createKey(Key key) {
            return (T) info.create(key.toTuple(types));
        }

        @Override
        public Key toKey(Object key) {
            return Key.create(info.toTuple(key));
        }

        @Override
        public byte[] toBytes(Key key) {
            return format.serialize(key.toArray());
        }

        @Override
        public Key read(byte[] data) {
            return Key.create(format.deserialize(data));
        }
    }

    static class PrimitiveKeyConstructor extends KeyConstructor {
        private final Class<?> keyClass;
        private final TypeUtil.TypeConv<?> conv;

        public PrimitiveKeyConstructor(Class<?> keyClass) {
            this.keyClass = keyClass;
            this.conv = TypeUtil.converterOf(keyClass);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T createKey(Key key) {
            return key.convert((Class<T>) keyClass);
        }

        @Override
        public Key toKey(Object key) {
            return Key.create(key);
        }

        @Override
        public byte[] toBytes(Key key) {
            return conv.toBytes(key.toArray()[0]);
        }

        @Override
        public Key read(byte[] data) {
            return Key.create(
                    conv.fromBytes(data));
        }

    }
}
