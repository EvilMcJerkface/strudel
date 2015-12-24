package com.nec.strudel.tkvs;

import com.nec.strudel.entity.KeyGeneratorType;

public final class KeyGen {

	private KeyGen() {
	}

	public static void generateKey(TransactionalDB db,
			final KeyGeneratorType keyGen, final Object entity) {
		String groupName = keyGen.getGroupName();
		Key gKey = Entities.toKey(keyGen.getGroupKey(entity));
		TransactionRunner.run(db, groupName, gKey,
				new TransactionTask<Void>() {
					@Override
					public Void run(Transaction tx) {
						generateKey(tx, keyGen, entity);
						return null;
					}
				});
	}
	public static void generateKey(Transaction tx,
			KeyGeneratorType keyGen, Object entity) {
		Object counterKey = keyGen.getGeneratorKey(entity);
		String name = keyGen.getName();
		Counter c = getCounter(tx, name, counterKey);
		keyGen.setKey(entity, c.nextValue());
		tx.put(name, c.getKey(), c.getRecord());
	}

	static Counter getCounter(Transaction tx, String name, Object key) {
		Key k = Entities.toKey(key);
		Record record = tx.get(name, k);
		if (record != null) {
			return new Counter(k, record);
		} else {
			return new Counter(k);
		}
	}
}
