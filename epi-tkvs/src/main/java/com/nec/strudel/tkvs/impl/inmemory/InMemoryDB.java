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
import com.nec.strudel.tkvs.TransactionalDB;
import com.nec.strudel.tkvs.impl.CollectionBuffer;
import com.nec.strudel.tkvs.impl.CollectionBufferImpl;
import com.nec.strudel.tkvs.impl.KVStore;
import com.nec.strudel.tkvs.impl.TransactionProfiler;

public class InMemoryDB implements TransactionalDB {
	private final String name;
	private final ConcurrentHashMap<GKey, InMemoryKVStore> stores
	= new ConcurrentHashMap<GKey, InMemoryKVStore>();
	private final BackoffPolicy backoff;

	public InMemoryDB(String name, BackoffPolicy backoff) {
		this.name = name;
		this.backoff = backoff;
	}
	public InMemoryDB(String name) {
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

	public InMemoryKVStore store(String groupName, Key groupKey) {
		GKey gk = new GKey(groupName, groupKey);
		InMemoryKVStore store = stores.get(gk);
		if (store == null) {
			store = new InMemoryKVStore(gk);
			InMemoryKVStore old = stores.putIfAbsent(gk, store);
			if (old != null) {
				return old;
			}
		}
		return store;
	}

	static class InMemoryKVStore implements KVStore, Committer {
	    private final GKey gkey;
		private volatile long version = 0;
		private final ConcurrentHashMap<GKey, Versioned> data =
			new ConcurrentHashMap<GKey, Versioned>();
		InMemoryKVStore(GKey gkey) {
		    this.gkey = gkey;
		}
		@Override
		public Record get(String name, Key key) {
			Versioned v = data.get(new GKey(name, key));
			if (v != null) {
				return v.getValue();
			} else {
				return null;
			}
		}
		public long getVersion() {
			return version;
		}
		public long getVersion(String name, Key key) {
			Versioned v = data.get(new GKey(name, key));
			if (v != null) {
				return v.getVersion();
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
					long v = this.getVersion(name, k);
					if (v > time) {
						return false;
					}
				}
			}
			version++;
			for (CollectionBuffer cb : buffers) {
				String name = cb.getName();
				for (Map.Entry<Key, Record> e
				        : cb.getWrites().entrySet()) {
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
