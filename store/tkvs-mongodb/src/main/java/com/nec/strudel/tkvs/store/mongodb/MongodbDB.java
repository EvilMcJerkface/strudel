package com.nec.strudel.tkvs.store.mongodb;

import com.mongodb.DBCollection;
import com.nec.strudel.entity.IsolationLevel;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Transaction;
import com.nec.strudel.tkvs.TransactionalDB;
import com.nec.strudel.tkvs.impl.TransactionProfiler;


public class MongodbDB implements TransactionalDB {
	private final DBCollection coll;
	private final String name;
	private final TransactionProfiler prof;
	private final BackoffPolicy policy;

	public MongodbDB(String dbname, DBCollection coll,
			TransactionProfiler prof, BackoffPolicy policy) {
		this.name = dbname;
		this.coll = coll;
		this.prof = prof;
		this.policy = policy;
	}
	@Override
	public Transaction start(String groupName, Key groupKey) {
		return new MongodbTransaction(groupName, groupKey, coll, prof);
	}

    @Override
    public Transaction start(String groupName,
            Key groupKey, IsolationLevel level) {
        return start(groupName, groupKey);
    }

    @Override
    public IsolationLevel maxIsolationLevel() {
        return IsolationLevel.SERIALIZABLE;
    }
    @Override
    public BackoffPolicy backoffPolicy() {
    	return policy;
    }

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void close() {
	}


}
