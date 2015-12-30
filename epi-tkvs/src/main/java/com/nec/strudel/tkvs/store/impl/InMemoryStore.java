package com.nec.strudel.tkvs.store.impl;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.nec.strudel.target.DatabaseCreator;
import com.nec.strudel.target.Target;
import com.nec.strudel.target.TargetLifecycle;
import com.nec.strudel.target.TargetUtil;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.TransactionalDB;
import com.nec.strudel.tkvs.impl.inmemory.InMemoryDB;
import com.nec.strudel.tkvs.store.TransactionalStore;

public class InMemoryStore implements TransactionalStore {
	private final ConcurrentMap<String, InMemoryDB> dbs =
		new ConcurrentHashMap<String, InMemoryDB>();

	@Override
	public Target<TransactionalDB> create(String dbName, Properties props) {
		InMemoryDB db = dbs.get(dbName);
		if (db == null) {
			db = new InMemoryDB(dbName, new BackoffPolicy(props));
			InMemoryDB old = dbs.putIfAbsent(dbName, db);
			if (old != null) {
				return TargetUtil.sharedTarget(
						(TransactionalDB) old);
			}
		}
		return TargetUtil.sharedTarget((TransactionalDB) db);
	}
	@Override
	public TargetLifecycle lifecycle(String dbName, Properties props) {
		return DatabaseCreator.NULL_CREATOR;
	}

}
