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

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import com.nec.strudel.entity.IndexType;

@NotThreadSafe
public abstract class IndexData {
    private static final int HASH_BASE = 31;
    private int hashCode;
    private final KeyConstructor kconst;
    private final String name;
    private final String groupName;
    private Key groupKey;
    private Key key;

    protected IndexData(IndexType type, Key groupKey, Key key) {
        this.name = type.getName();
        this.groupName = type.getGroupName();
        this.kconst = Entities.getKeyConstructorOf(
                type.targetKeyClass());
        this.groupKey = groupKey;
        this.key = key;
    }

    public static IndexData create(IndexType type, Key groupKey,
            Key key, Record record) {
        if (type.isAuto()) {
            return new AutoIndexData(type, groupKey, key, record.toBytes());
        } else {
            return new NormalIndexData(type, groupKey, key,
                    KeyArray.create(type, record.toBytes()));
        }
    }

    public static IndexData create(IndexType type, Key groupKey, Key key) {
        if (type.isAuto()) {
            return new AutoIndexData(type, groupKey, key);
        } else {
            return new NormalIndexData(type, groupKey, key, KeyArray.empty(type));
        }
    }

    public String getName() {
        return name;
    }

    public String getGroupName() {
        return groupName;
    }

    public Key getGroupKey() {
        return groupKey;
    }

    public Key getKey() {
        return key;
    }

    public abstract void insert(Key ref);


    public abstract void remove(Key ref);

    public abstract int size();

    public abstract boolean isEmpty();

    public <T> Iterable<T> scan(Class<T> itemClass) {
        final IndexData idx = this;
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new EntityIdIterator<T>(idx);
            }
        };
    }

    public abstract Object createNewKey();

    public abstract <T> T getTargetKey(int index);

    public abstract byte[] toBytes();

    protected Record toRecord() {
        return SimpleRecord.create(toBytes());
    }

    protected KeyConstructor constructor() {
        return kconst;
    }

    protected abstract int contentHashCode();

    protected abstract boolean contentEquals(IndexData data);

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = getClass().hashCode();
            hashCode = hashCode * HASH_BASE
                    + contentHashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass().equals(obj.getClass())) {
            IndexData data = (IndexData) obj;
            if (!key.equals(data.key)) {
                return false;
            }
            if (!groupKey.equals(data.groupKey)) {
                return false;
            }
            return contentEquals(data);
        }
        return false;
    }

    public static class NormalIndexData extends IndexData {
        private KeyArray value;

        protected NormalIndexData(IndexType type, Key groupKey, Key key,
                KeyArray value) {
            super(type, groupKey, key);
            this.value = value;
        }

        @Override
        public int size() {
            return value.size();
        }

        @Override
        public boolean isEmpty() {
            return value.size() == 0;
        }

        @Override
        public void insert(Key ref) {
            value = value.insert(ref);
        }

        @Override
        public void remove(Key ref) {
            value = value.remove(ref);
        }

        @Override
        public Object createNewKey() {
            throw new UnsupportedOperationException(
                    "auto ID generation not supported for this index");
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getTargetKey(int index) {
            return (T) constructor()
                    .createKey(value.getKey(index));
        }

        @Override
        public byte[] toBytes() {
            return value.toBytes();
        }

        @Override
        protected int contentHashCode() {
            return value.hashCode();
        }

        @Override
        protected boolean contentEquals(IndexData data) {
            if (data instanceof NormalIndexData) {
                return value.equals(((NormalIndexData) data).value);
            }
            return false;
        }
    }

    static class EntityIdIterator<T> implements Iterator<T> {
        private int current = 0;
        private final IndexData idx;

        EntityIdIterator(IndexData idx) {
            this.idx = idx;
        }

        @Override
        public boolean hasNext() {
            return current < idx.size();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T key = idx.getTargetKey(current);
            current++;
            return key;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
