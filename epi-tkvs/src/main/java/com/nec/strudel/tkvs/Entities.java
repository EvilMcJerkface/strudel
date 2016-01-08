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

import java.util.concurrent.ConcurrentHashMap;

import com.nec.strudel.entity.EntityDescriptor;
import com.nec.strudel.entity.IsolationLevel;

public final class Entities {
	private Entities() {
		// not instantiated
	}
    public static <T> T get(Transaction tx, Class<T> c,
    		EntityDescriptor e, Object key) {
		String name = e.getName();
	    Record record = tx.get(name, toKey(key));
	    if (record != null) {
	        return create(c, record);
	    } else {
	        return null;
	    }
	}

    public static <T> T get(TransactionalDB db,
    		final Class<T> c, final EntityDescriptor e,
			Object groupKey, final Object key) {
		String groupName = e.getGroupName();
	    return TransactionRunner.run(db,
	    		/**
	    		 * For reading single key-value,
	    		 * read-committed is same as
	    		 * serializable
	    		 */
	    		IsolationLevel.READ_COMMITTED,
	    		groupName,
	    		toKey(groupKey),
	    		new TransactionTask<T>() {
					@Override
					public T run(Transaction tx) {
						return get(tx, c, e, key);
					}
	    });
	}

	public static void store(Transaction tx, EntityDescriptor desc,
			Object entity) {
		String gName = desc.getGroupName();
		Object key = desc.getKey(entity);
		Key gKey = toKey(desc.toGroupKey(key));
	    if (!tx.getName().equals(gName)) {
	        throw new TransactionException(
	          "transaction group mismatch entity="
	           + gName
	           + " transaction=" + tx.getName());
	    }
	    if (!tx.getKey().equals(gKey)) {
	        throw new TransactionException(
	                "transaction key mismatch entity="
	                 + gKey
	                 + " transaction=" + tx.getKey());
	    }
	    String name = desc.getName();
	    tx.put(name, toKey(key), Entities.toRecord(entity));
	}


	public static void store(TransactionalDB db,
			EntityDescriptor desc, final Object entity) {
		String gName = desc.getGroupName();
		final Object key = desc.getKey(entity);
		Object gKey = desc.toGroupKey(key);
		final String name = desc.getName();

		TransactionRunner.run(db,
				/**
				 * For updating one key-value,
				 * snapshot isolation is same
				 * as serializable
				 */
				IsolationLevel.SNAPSHOT,
				gName, toKey(gKey),
	    		new TransactionTask<Void>() {
	        @Override
	        public Void run(Transaction tx) {
	    	    tx.put(name, toKey(key),
	    	    		Entities.toRecord(entity));
	            return null;
	        }
	    });
	}

    private static <T> T create(Class<T> c, Record record) {
		EntityConstructor cons = getConstructorOf(c);
		return cons.create(record);
    }
   	private static EntityConstructor getConstructorOf(Class<?> c) {
    	EntityConstructor cons = CONSTS.get(c);
    	if (cons == null) {
    		cons = new EntityConstructor(c);
    		CONSTS.put(c, cons);
    	}
    	return cons;
    }
   	protected static KeyConstructor getKeyConstructorOf(Class<?> c) {
   		KeyConstructor cons = K_CONSTS.get(c);
    	if (cons == null) {
    		cons = KeyConstructor.constructorOf(c);
    		K_CONSTS.put(c, cons);
    	}
    	return cons;
    }
    private static final ConcurrentHashMap<Class<?>,
    						EntityConstructor> CONSTS =
    		new ConcurrentHashMap<Class<?>, EntityConstructor>();

    private static final ConcurrentHashMap<Class<?>,
    						KeyConstructor> K_CONSTS =
    		new ConcurrentHashMap<Class<?>, KeyConstructor>();

    protected static Key toKey(Object key) {
		KeyConstructor c =
				getKeyConstructorOf(key.getClass());
		return c.toKey(key);
    }

	private static Record toRecord(Object entity) {
		EntityConstructor c =
					getConstructorOf(
							entity.getClass());
		return c.toRecord(entity);
	}


	public static void delete(Transaction tx, EntityDescriptor e, Object key) {
		String name = e.getName();
		tx.delete(name, toKey(key));
	}

	public static void delete(TransactionalDB db, final EntityDescriptor e,
			final Object groupKey, final Object key) {
		String groupName = e.getGroupName();
	    TransactionRunner.run(db, groupName, toKey(groupKey),
	    		new TransactionTask<Void>() {
					@Override
					public Void run(Transaction tx) {
						delete(tx, e, key);
						return null;
					}
	    });
	}

}
