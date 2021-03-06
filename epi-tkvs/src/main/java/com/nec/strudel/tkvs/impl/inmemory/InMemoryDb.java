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

package com.nec.strudel.tkvs.impl.inmemory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.nec.strudel.entity.IsolationLevel;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;
import com.nec.strudel.tkvs.Transaction;
import com.nec.strudel.tkvs.TransactionManager;
import com.nec.strudel.tkvs.impl.CollectionBuffer;
import com.nec.strudel.tkvs.impl.CollectionBufferImpl;
import com.nec.strudel.tkvs.impl.KeyValueReader;
import com.nec.strudel.tkvs.impl.TransactionProfiler;

public class InMemoryDb implements TransactionManager {
    private final String name;
    private final ConcurrentHashMap<GKey, InMemoryKvStore> stores =
            new ConcurrentHashMap<GKey, InMemoryKvStore>();
    private final BackoffPolicy backoff;

    public InMemoryDb(String name, BackoffPolicy backoff) {
        this.name = name;
        this.backoff = backoff;
    }

    public InMemoryDb(String name) {
        this.name = name;
        this.backoff = new BackoffPolicy();
    }

    @Override
    public Transaction start(String groupName, Key groupKey) {
        return store(groupName, groupKey).start();
    }

    @Override
    public Transaction start(String groupName, Key groupKey,
            IsolationLevel level) {
        // TODO utilize isolation level
        return store(groupName, groupKey).start();
    }

    @Override
    public IsolationLevel maxIsolationLevel() {
        return IsolationLevel.SERIALIZABLE;
    }

    @Override
    public BackoffPolicy backoffPolicy() {
        return backoff;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void close() {
    }

    public InMemoryKvStore store(String groupName, Key groupKey) {
        GKey gk = new GKey(groupName, groupKey);
        InMemoryKvStore store = stores.get(gk);
        if (store == null) {
            store = new InMemoryKvStore(gk);
            InMemoryKvStore old = stores.putIfAbsent(gk, store);
            if (old != null) {
                return old;
            }
        }
        return store;
    }

    static class InMemoryKvStore implements KeyValueReader, Committer {
        private final GKey gkey;
        private volatile long version = 0;
        private final ConcurrentHashMap<GKey, Versioned> data =
                new ConcurrentHashMap<GKey, Versioned>();

        InMemoryKvStore(GKey gkey) {
            this.gkey = gkey;
        }

        @Override
        public Record get(String name, Key key) {
            Versioned val = data.get(new GKey(name, key));
            if (val != null) {
                return val.getValue();
            } else {
                return null;
            }
        }

        public long getVersion() {
            return version;
        }

        public long getVersion(String name, Key key) {
            Versioned val = data.get(new GKey(name, key));
            if (val != null) {
                return val.getVersion();
            } else {
                return 0;
            }
        }

        protected void put(String name, Key key, long version,
                Record value) {
            data.put(new GKey(name, key),
                    new Versioned(version, value));
        }

        public synchronized InMemoryTransaction start() {
            return new InMemoryTransaction(
                    gkey.getName(), gkey.getKey(),
                    this, version, this,
                    TransactionProfiler.NO_PROF);
        }

        @Override
        public synchronized boolean commit(long time,
                Collection<CollectionBufferImpl> buffers) {
            for (CollectionBuffer cb : buffers) {
                String name = cb.getName();
                for (Key k : cb.getReads()) {
                    long val = this.getVersion(name, k);
                    if (val > time) {
                        return false;
                    }
                }
            }
            version++;
            for (CollectionBuffer cb : buffers) {
                String name = cb.getName();
                for (Map.Entry<Key, Record> e : cb.getWrites().entrySet()) {
                    this.put(name, e.getKey(),
                            version, e.getValue());
                }
            }
            return true;
        }
    }

    static final int HASH_BASE = 31;

    static class GKey {
        private final String name;
        private final Key key;

        GKey(String name, Key key) {
            this.name = name;
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public Key getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return name.hashCode() * HASH_BASE
                    + key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof GKey) {
                GKey gk = (GKey) obj;
                return (name.equals(gk.name)
                        && key.equals(gk.key));
            }
            return false;
        }
    }

    static class Versioned {
        private final long version;
        private final Record value;

        public Versioned(long version,
                @Nullable Record value) {
            this.version = version;
            this.value = value;
        }

        public Record getValue() {
            return value;
        }

        public long getVersion() {
            return version;
        }
    }

}
