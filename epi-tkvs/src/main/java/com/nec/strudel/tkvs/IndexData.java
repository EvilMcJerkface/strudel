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
public class IndexData {
	private static final int HASH_BASE = 31;
	private int hashCode;
	private final boolean auto;
	private final KeyConstructor kconst;
	private final String name;
	private final String groupName;
    private Key groupKey;
	private Key key;
	private Record record;

	protected IndexData(IndexType type, Key groupKey,
	        Key key, Record record) {
		this.name = type.getName();
		this.groupName = type.getGroupName();
		this.auto = type.isAuto();
		this.kconst = Entities.getKeyConstructorOf(
				type.targetKeyClass());
	    this.groupKey = groupKey;
		this.key = key;
		this.record = record;
	}
	protected IndexData(IndexType type, Key groupKey, Key key) {
		this(type, groupKey, key, Record.create());
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
	public void insert(Key ref) {
		if (auto) {
    		throw new RuntimeException(
    		"cannot insert to auto-increment index:"
    		+ name);
		}
		add(ref);
	}
	private void add(Object ref) {
		String refStr = ref.toString();
		for (String s : record.values()) {
			if (refStr.equals(s)) {
				return;
			}
		}
		record = record.append(refStr);
	}
	public void remove(Key ref) {
		if (auto) {
    		throw new RuntimeException(
    		"cannot remove from auto-increment index:"
    		+ name);
		}
		String refStr = ref.toString();
		int idx = -1;
		for (int i = 0; i < record.size(); i++) {
			if (refStr.equals(record.get(i))) {
				idx = i;
				break;
			}
		}
		if (idx >= 0) {
			record = record.removeAt(idx);
		}
	}


	public Key getKey() {
		return key;
	}

	public int size() {
	    return record.size();
	}
	public boolean isEmpty() {
	    return record.size() == 0;
	}
	public Key get(int index) {
	   return Key.parse(record.get(index));
	}

	public <T> Iterable<T> scan(Class<T> itemClass) {
		final IndexData idx = this;
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new EntityIdIterator<T>(idx);
			}
		};
	}
	public Object addNewKeyEntry() {
		Object entry = newKeyEntry();
		add(entry);
		return entry;
	}

	private Object newKeyEntry() {
		if (auto) {
			return autoIncrementInt();
		}
		throw new UnsupportedOperationException(
		"auto ID generation not supported for this index");
	}
	public Object toTargetKey(Key key, Key entry) {
		if (auto) {
			return kconst.createKey(key.concat(entry));
		} else {
			return kconst.createKey(entry);
		}
	}

	protected Record getRecord() {
		return record;
	}

	protected int autoIncrementInt() {
		if (this.isEmpty()) {
			return 1;
		} else {
			/**
			 * NOTE assumes that auto increment int
			 * are inserted in order.
			 */
			Key lastKey = get(size() - 1);
			try {
				return lastKey.toInt() + 1;
			} catch (NumberFormatException e) {
				throw new NumberFormatException(
				"key cannot be parsed as an integer: "
				+ lastKey);
			}
		}
	}

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = getClass().hashCode();
            hashCode = hashCode * HASH_BASE
                    + record.hashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass().equals(obj.getClass())) {
            IndexData e = (IndexData) obj;
            return record.equals(e.record);
        }
        return false;
    }

	static class EntityIdIterator<T> implements Iterator<T> {
		private int current = 0;
		private final Record rec;
		private final IndexData idx;
		EntityIdIterator(IndexData idx) {
			this.rec = idx.getRecord();
			this.idx = idx;
		}
		@Override
		public boolean hasNext() {
			return current < rec.size();
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			String value = rec.get(current);
			current++;
			return (T) idx.toTargetKey(idx.getKey(),
					Key.parse(value));
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
