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

import static com.nec.strudel.tkvs.store.tokumx.TokumxStore.TokumxDbServer.DOCNAME;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.nec.strudel.target.DatabaseCreator;
import com.nec.strudel.tkvs.TkvStoreException;
public class TokumxLifecycle extends DatabaseCreator {
	private final String dbName;
	private final String[] mongoDs;
	public TokumxLifecycle(String dbName, String... mongoDs) {
		this.dbName = dbName;
		this.mongoDs = mongoDs;
	}

	@Override
	public void close() {
	}

	@Override
	public void initialize() {
		for (int i = 0; i < mongoDs.length; i++) {
			try {
				MongoClient client = new MongoClient(mongoDs[i]);
				client.getDB(dbName).getCollection(dbName)
				.createIndex(new BasicDBObject(DOCNAME, 1),
					new BasicDBObject("unique", true));
				client.close();
			} catch (UnknownHostException e) {
				throw new TkvStoreException("unknown host:" + mongoDs[i]);
			}
		}
	}

	@Override
	public void prepare() {

	}

}
