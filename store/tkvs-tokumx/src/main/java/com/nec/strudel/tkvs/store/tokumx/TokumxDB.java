/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
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
package com.nec.strudel.tkvs.store.tokumx;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.nec.strudel.entity.IsolationLevel;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Transaction;
import com.nec.strudel.tkvs.TransactionManager;
import com.nec.strudel.tkvs.impl.TransactionProfiler;


public class TokumxDB implements TransactionManager {
	private final MongoClient[] mclients;
	private final int numMongod;
	private final String name;
	private final TransactionProfiler prof;
	private final BackoffPolicy policy;

	public TokumxDB(String dbname, int numMongod, MongoClient[] mclients,
			TransactionProfiler prof, BackoffPolicy policy) {
		this.name = dbname;
		this.mclients = mclients;
		this.numMongod = numMongod;
		this.prof = prof;
		this.policy = policy;
	}

	@Override
	public Transaction start(String groupName, Key groupKey) {
		int index =
            Math.abs((groupName
            	+ groupKey).hashCode() % numMongod);
		DB db = null;
		DBCollection coll = null;
		db = mclients[index].getDB(name);
		//mongoClient.connect( new DBAddress(mongoDs[index], name));
		coll = db.getCollection(name);
		return new TokumxTransaction(groupName,
				groupKey, name, db, coll, prof);
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
