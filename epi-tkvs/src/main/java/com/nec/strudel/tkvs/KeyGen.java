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
