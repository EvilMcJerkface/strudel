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
package com.nec.strudel.tkvs.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;

public class CollectionBufferImpl implements CollectionBuffer {
	private final String name;
	private final KVStore store;
	private final Map<Key, Record> records =
		new HashMap<Key, Record>();
	private final Set<Key> reads = new HashSet<Key>();
	private final Set<Key> writes = new HashSet<Key>();
	private final TransactionProfiler prof;
	public CollectionBufferImpl(String name,
			KVStore store, TransactionProfiler prof) {
		this.name = name;
		this.store = store;
		this.prof = prof;
	}
	@Override
    public String getName() {
		return name;
	}
	@Override
    public void load(Key key, Record record) {
		records.put(key, record);
	}
	@Override
    public Record get(Key key) {
		reads.add(key);
		if (records.containsKey(key)) {
			prof.getInBuffer(name);
			return records.get(key);
		}
		prof.getStart(name);
		Record r = store.get(name, key);
		prof.getDone(name);
		if (r != null) {
			r.toString();
		}
		records.put(key, r);
		return r;
	}
	@Override
    public void put(Key key, Record record) {
		reads.add(key);
		writes.add(key);
		records.put(key, record);
	}
	@Override
    public void delete(Key key) {
		reads.add(key);
		writes.add(key);
		records.put(key, null);
	}
	@Override
    public Set<Key> getReads() {
		return reads;
	}
	@Override
    public Map<Key, Record> getWrites() {
		Map<Key, Record> result =
			new HashMap<Key, Record>();
		for (Key k : writes) {
			result.put(k, records.get(k));
		}
		return result;
	}
}
