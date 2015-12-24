package com.nec.strudel.tkvs;

import java.util.ArrayList;
import java.util.List;

import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityDescriptor;
import com.nec.strudel.entity.EntityGroup;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.entity.IndexType;
import com.nec.strudel.entity.KeyGeneratorType;

public class EntityDBImpl implements EntityDB {
	private final TransactionalDB db;
	public EntityDBImpl(TransactionalDB db) {
		this.db = db;
	}
	@Override
	public <T> T get(Class<T> c, Object key) {
		EntityDescriptor desc = EntityGroup.descriptor(c);
		Object gKey = desc.toGroupKey(key);
		return Entities.get(db, c, desc, gKey, key);
	}

	@Override
	public void update(Object entity) {
		EntityDescriptor desc = EntityGroup.descriptor(
				entity.getClass());
		Entities.store(db, desc, entity);
	}

	@Override
	public void create(final Object entity) {
		final EntityDescriptor desc = EntityGroup.descriptor(
				entity.getClass());
		if (desc.hasGeneratedGroupKey()) {
			IndexType index = desc.autoIndex();
			final Object key = Indexes.newKey(db, index,
					index.getIndexKey(entity));
			desc.setKey(entity, key);
			TransactionRunner.run(db, desc.getGroupName(),
					Entities.toKey(desc.toGroupKey(key)),
					new TransactionTask<Void>() {
						@Override
						public Void run(Transaction tx) {
							indexAndStore(tx, db, desc,
									key, entity);
							return null;
						}
			});

		} else {
		   	Object key = desc.getKey(entity);
	    	Object gKey = desc.toGroupKey(key);
			TransactionRunner.run(db, desc.getGroupName(),
					Entities.toKey(gKey),
					new TransactionTask<Void>() {
						@Override
						public Void run(Transaction tx) {
							create(tx, db, desc, entity);
							return null;
						}
			});
		}
	}
	@Override
	public void delete(Object entity) {
		EntityDescriptor desc = EntityGroup.descriptor(entity.getClass());
		Object key = desc.getKey(entity);
		Object gKey = desc.toGroupKey(key);
		Entities.delete(db, desc, gKey, key);
		unindex(db, desc, key, entity);
	}

	@Override
	public <T> Iterable<T> scanIds(Class<T> idClass,
			Class<?> entityClass, String property,
			Object key) {
		IndexType type = IndexType.on(entityClass, property);
		IndexData idx = Indexes.get(db, type, key);
		return idx.scan(idClass);
	}



	@Override
	public <T> List<T> getEntitiesByIndex(Class<T> ec, String property,
			Object key) {
		EntityDescriptor desc = EntityGroup.descriptor(ec);
		IndexType type = desc.indexOn(property);
		if (type == null) {
			throw new RuntimeException("no index on " + property
					+ ": " + ec);
		}
		List<T> res = new ArrayList<T>();
		IndexData idx = Indexes.get(db, type, key);
		Class<?> keyClass = desc.getKeyClass();
		for (Object k : idx.scan(keyClass)) {
			T entity = get(ec, k);
			if (entity != null) {
				res.add(entity);
			}
		}
		return res;
	}


	@Override
	public <T> T run(Object entity, final EntityTask<T> task) {
		EntityDescriptor desc = EntityGroup.descriptor(entity.getClass());
	   	String gName = desc.getGroupName();
	   	Object key = desc.getKey(entity);
    	Object gKey = desc.toGroupKey(key);
 		return TransactionRunner.run(db, gName, Entities.toKey(gKey),
 				new TransactionTask<T>() {
					@Override
					public T run(Transaction tx) {
						return task.run(
						new EntityTransactionImpl(tx, db));
					}
 		});
	}
	@Override
	public <T> T run(Class<?> entityClass,
			Object key, final EntityTask<T> task) {
		EntityDescriptor desc = EntityGroup.descriptor(entityClass);
	   	String gName = desc.getGroupName();
		Object gKey = desc.toGroupKey(key);
 		return TransactionRunner.run(db, gName, Entities.toKey(gKey),
 				new TransactionTask<T>() {
					@Override
					public T run(Transaction tx) {
						return task.run(
						new EntityTransactionImpl(tx, db));
					}
 		});
	}

	static void create(Transaction tx, TransactionalDB db,
			EntityDescriptor desc, Object entity) {
		KeyGeneratorType keyGen = desc.keyGenerator();
		if (keyGen != null) {
			create(tx, db, keyGen, desc, entity);
			return;
		}
		IndexType index = desc.autoIndex();

		Object key;
		if (index == null) {
			key = desc.getKey(entity);
		} else if (index.isInGroup()
				&& !desc.hasExternalIndex()) {
			/**
			 * new key can be created in the
			 * same transaction
			 */
			key = Indexes.newKey(tx, index,
					index.getIndexKey(entity));
			desc.setKey(entity, key);
		} else {
			/**
			 * gen KEY outside of TX
			 */
			key = Indexes.newKey(db, index,
					index.getIndexKey(entity));
			desc.setKey(entity, key);
		}
		indexAndStore(tx, db, desc, key, entity);
	}
	static void create(Transaction tx, TransactionalDB db,
			KeyGeneratorType keyGen, EntityDescriptor desc, Object entity) {
		if (keyGen.isInGroup()) {
			KeyGen.generateKey(tx, keyGen, entity);
			Object key = desc.getKey(entity);
			indexAndStore(tx, db, desc, key, entity);
		} else {
			KeyGen.generateKey(db, keyGen, entity);
			Object key = desc.getKey(entity);
			indexAndStore(tx, db, desc, key, entity);
		}
	}
	static void indexAndStore(Transaction tx, TransactionalDB db,
			EntityDescriptor desc, Object key, Object entity) {
		try {
			index(tx, db, desc, key, entity);
			Entities.store(tx, desc, entity);
		} catch (RuntimeException e) {
			unindex(db, desc, key, entity);
			throw e;
		}
	}
	static void index(Transaction tx, TransactionalDB db,
			EntityDescriptor desc,
			final Object key, final Object entity) {
		for (IndexType index : desc.index()) {
			if (!index.isAuto()) {
				final Object idxKey = index.getIndexKey(entity);
				if (index.isInGroup()) {
					Indexes.insert(tx, index, idxKey, key);
				} else {
					final IndexType idx = index;
			    	Object groupKey = idx.toGroupKey(idxKey);
			    	String gName = idx.getGroupName();
			        TransactionRunner.run(db, gName,
			        		Entities.toKey(groupKey),
			          new TransactionTask<Void>() {
			            @Override
			            public Void run(Transaction tx) {
			            	Indexes.insert(tx, idx, idxKey, key);
			            	return null;
			            }
			            @Override
			            public String toString() {
			            	StringBuilder sb = new StringBuilder()
			            	.append("index:").append(idx.getName())
			            			.append(" key =" + key)
			            			.append(" ref=" + key)
			            			.append(" entity=" + entity);
			            	return sb.toString();
			            }
			        });
				}
			}
		}
	}
	static void unindex(TransactionalDB db,
			EntityDescriptor desc, Object key, Object entity) {
		for (IndexType index : desc.index()) {
			if (!index.isAuto()) {
				Object idxKey = index.getIndexKey(entity);
				Indexes.remove(db, index, idxKey, key);
			}
		}
	}

	static class EntityTransactionImpl implements EntityTransaction {
		private final Transaction tx;
		private final TransactionalDB db;
		EntityTransactionImpl(Transaction tx, TransactionalDB db) {
			this.tx = tx;
			this.db = db;
		}
		@Override
		public <T> T get(Class<T> c, Object key) {
			return Entities.get(tx, c, EntityGroup.descriptor(c), key);
		}

		@Override
		public void update(Object entity) {
			EntityDescriptor desc = EntityGroup.descriptor(entity.getClass());
			Entities.store(tx, desc, entity);
		}
		@Override
		public void create(Object entity) {
			EntityDescriptor desc = EntityGroup.descriptor(entity.getClass());
			EntityDBImpl.create(tx, db, desc, entity);
		}
		@Override
		public void delete(Object entity) {
			EntityDescriptor desc =
				EntityGroup.descriptor(entity.getClass());
			Object key = desc.getKey(entity);
			Entities.delete(tx, desc, key);
			unindex(db, desc, key, entity);
		}
	}
}
