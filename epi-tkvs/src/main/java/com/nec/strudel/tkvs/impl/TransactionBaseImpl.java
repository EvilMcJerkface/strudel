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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;
import com.nec.strudel.tkvs.Transaction;

public abstract class TransactionBaseImpl implements Transaction {
    private final Map<String, CollectionBufferImpl> buffers =
    	new HashMap<String, CollectionBufferImpl>();
    private final KVStore store;
    private final String name;
    private final Key key;
    private final TransactionProfiler prof;
    public TransactionBaseImpl(String name, Key key, KVStore store,
    		TransactionProfiler prof) {
        this.name = name;
        this.key = key;
    	this.store = store;
    	this.prof = prof;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public Key getKey() {
        return key;
    }

	@Override
	public Record get(String name, Key key) {
		return getBuffer(name).get(key);
	}

	@Override
	public void put(String name, Key key, Record value) {
		getBuffer(name).put(key, value);
	}

	@Override
	public void delete(String name, Key key) {
		getBuffer(name).delete(key);
	}

	protected Collection<CollectionBufferImpl> buffers() {
		return buffers.values();
	}

	protected CollectionBuffer getBuffer(String name) {
		CollectionBufferImpl buff = buffers.get(name);
		if (buff == null) {
			buff = new CollectionBufferImpl(name,
					store, prof);
			buffers.put(name, buff);
		}
		return buff;
	}

	protected KVStore getStore() {
		return store;
	}
}
