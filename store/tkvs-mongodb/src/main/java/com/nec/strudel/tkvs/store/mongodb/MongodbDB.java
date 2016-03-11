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
package com.nec.strudel.tkvs.store.mongodb;

import com.mongodb.DBCollection;
import com.nec.strudel.entity.IsolationLevel;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Transaction;
import com.nec.strudel.tkvs.TransactionManager;
import com.nec.strudel.tkvs.impl.TransactionProfiler;


public class MongodbDB implements TransactionManager {
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
