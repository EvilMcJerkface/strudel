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

import java.io.IOException;
import java.util.Properties;

import com.mongodb.MongoClient;
import com.nec.strudel.target.TargetLifecycle;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.TkvStoreException;
import com.nec.strudel.tkvs.impl.TransactionProfiler;
import com.nec.strudel.tkvs.impl.TransactionalDbServer;
import com.nec.strudel.tkvs.store.TransactionalStore;
import com.nec.strudel.tkvs.store.impl.AbstractTransactionalStore;

public class TokumxStore extends AbstractTransactionalStore implements TransactionalStore {
	@Override
	public TokumxDbServer createDbServer(String dbName, Properties props) {
		
		String mongos = props.getProperty("mongodb.mongos", "localhost");
		try {
			return new TokumxDbServer(dbName, mongos,
					new BackoffPolicy(props));
		} catch (IOException e) {
			throw new TkvStoreException(
			"TokumxStore failed to create (IOException)", e);
		}
	}
	@Override
	public TargetLifecycle lifecycle(String dbName, Properties props) {
		String mongos = props.getProperty("mongodb.mongos", "localhost");
		return new TokumxLifecycle(dbName, mongos.split(","));
	}

	public static class TokumxDbServer implements TransactionalDbServer<TransactionProfiler> {
		private final MongoClient[] mclients;
		private final String dbName;
		public static final String DOCNAME = "!entity + ekey!";
		public static final String VALUENAME = "!value!";
		private final String[] mongoDs;
		private final int numMongod;
		private final BackoffPolicy policy;
		public TokumxDbServer(String dbName, String mongods,
				BackoffPolicy policy)
				throws IOException {
			this.dbName = dbName;
			this.policy = policy;
			this.mongoDs = mongods.split(",");
			this.numMongod = mongoDs.length;

            //Use one mongoclient per mongod, send
			//transactions directly to one mongod.
			this.mclients = new MongoClient[numMongod];
			for (int i = 0; i < numMongod; i++) {
				mclients[i] = new MongoClient(mongoDs[i]);
				/**
				 * NOTE initialization is done by
				 * TokumxLifecycle
				 */
			}
		}

		@Override
		public TokumxDB open(TransactionProfiler prof) {
			return new TokumxDB(dbName, numMongod, mclients, prof, policy);
		}

		@Override
		public void close() {
			for (int i = 0; i < numMongod; i++) {
				mclients[i].close();
			}
		}
	}
}
