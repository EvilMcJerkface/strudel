package com.nec.strudel.tkvs.store.test;

import java.util.Properties;

import com.nec.strudel.tkvs.TransactionalDB;
import com.nec.strudel.tkvs.store.impl.InMemoryStore;
import com.nec.strudel.tkvs.store.test.TKVStoreTestBase;

public class InMemoryKVStoreTest extends TKVStoreTestBase {
	static final InMemoryStore STORE = new InMemoryStore();
	private Properties props = new Properties();

	@Override
	public TransactionalDB getDB(String name) {
		return STORE.create(name, props).open();
	}

}
