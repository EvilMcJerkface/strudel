package com.nec.strudel.tkvs;

import javax.annotation.Nullable;

import com.nec.strudel.entity.IndexType;
import com.nec.strudel.entity.IsolationLevel;

public final class Indexes {
    private Indexes() {
        // not instantiated
    }

    protected static void store(Transaction tx, IndexData idx) {
    	String gName = idx.getGroupName();
        if (!tx.getName().equals(gName)) {
            throw new TransactionException(
              "transaction group mismatch entity="
               + gName
               + " transaction=" + tx.getName());
        }
        if (!tx.getKey().equals(idx.getGroupKey())) {
            throw new TransactionException(
                    "transaction key mismatch entity="
                     + idx.getGroupKey()
                     + " transaction=" + tx.getKey());
        }
        String name = idx.getName();
        tx.put(name, idx.getKey(), idx.getRecord());
    }

	public static IndexData get(TransactionalDB db,
			final IndexType type, final Object key) {
	   	Object groupKey = type.toGroupKey(key);
    	String gName = type.getGroupName();
        return TransactionRunner.run(db,
        		/**
        		 * In the current implementation an
        		 * index data on one key is just one
        		 * key-value record (so read committed
        		 * is good enough)
        		 */
        		IsolationLevel.READ_COMMITTED,
        		gName,
        		Entities.toKey(groupKey),
            new TransactionTask<IndexData>() {
			    @Override
				public IndexData run(Transaction tx) {
				  return get(tx, type, key);
				}
        });
	}
	private static IndexData get(Transaction tx,
			IndexType type, Object key) {
		Record r = Indexes.getRecord(tx, type, key);
		if (r != null) {
			return create(type, tx.getKey(),
					Entities.toKey(key), r);
		} else {
			return create(type, tx.getKey(), Entities.toKey(key));
		}
	}
    public static void remove(TransactionalDB db,
            final IndexType type, final Object key, final Object ref) {
    	Object groupKey = type.toGroupKey(key);
    	String gName = type.getGroupName();
        TransactionRunner.run(db, gName,
        		Entities.toKey(groupKey),
          new TransactionTask<Void>() {
            @Override
            public Void run(Transaction tx) {
            	remove(tx, type, key, ref);
            	return null;
            }
        });
    }

    public static void insert(Transaction tx,
            final IndexType type, final Object key, final Object ref) {
    	IndexData idx = Indexes.get(tx, type, key);
    	idx.insert(Entities.toKey(ref));
    	store(tx, idx);
    }
    public static void remove(Transaction tx,
            final IndexType type, final Object key, final Object ref) {
    	IndexData idx = Indexes.get(tx, type, key);
    	idx.remove(Entities.toKey(ref));
    	store(tx, idx);
    }

    public static Object newKey(TransactionalDB db,
    		final IndexType type,
    		final Object key) {
    	Object gKey = type.toGroupKey(key);
    	String gName = type.getGroupName();
	    return TransactionRunner.run(db, gName,
	    		Entities.toKey(gKey),
		  	      new TransactionTask<Object>() {
		              @Override
		              public Object run(Transaction tx) {
		            	  return newKey(tx, type, key);
		              }
		  	    });
    }
   	public static Object newKey(Transaction tx,
   			IndexType type, Object key) {
    	IndexData idx = get(tx, type, key);
    	Object newKey = idx.addNewKeyEntry();
    	store(tx, idx);
    	return idx.toTargetKey(idx.getKey(),
    			Entities.toKey(newKey));

    }
	private static IndexData create(
			IndexType type, Key groupKey, Key key) {
		return new IndexData(type, groupKey, key);
	}
	private static IndexData create(IndexType type,
			Key groupKey, Key key, Record r) {
		return new IndexData(type, groupKey, key, r);
	}
	@Nullable
	private static Record getRecord(Transaction tx,
			IndexType type, Object key) {
		String name = type.getName();
		return tx.get(name, Entities.toKey(key));
	}

}
