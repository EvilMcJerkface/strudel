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

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.nec.strudel.target.DatabaseCreator;

import static com.nec.strudel.tkvs.store.mongodb.MongodbStore.MongoDbServer.DOCNAME;

public class MongodbLifecycle extends DatabaseCreator {
	private static final Logger LOGGER = Logger.getLogger(MongodbLifecycle.class);
	public static final String PROP_INIT_CHUNK =
			"tkvs.mongodb.init.chunk.num";
	public static final String PROP_SLEEP_AFTER_SEC =
			"tkvs.mongodb.postpopulate.sleep.sec";

	private final String dbName;
	private final boolean sharded;
	private final MongoClient mongoClient;
	private final Properties props;

	public MongodbLifecycle(String dbName, ServerAddress host,
			boolean sharded, Properties props) {
		this.dbName = dbName;
		this.mongoClient = new MongoClient(host);
		this.sharded = sharded;
		this.props = props;
	}

	@Override
	public void close() {
		mongoClient.close();
	}
	protected int getIntOpt(String name, int defaultValue) {
		String value = props.getProperty(name);
		if (value != null && !value.trim().isEmpty()) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}

	@Override
	public void initialize() {
		DB db = mongoClient.getDB(dbName);
		DBCollection coll = db.getCollection(dbName);
		//create unique index on field docname,
		//this will have 2 effects:
		//1. Detect duplicate key upsertion when a
		//lot of initial transactions(meaning document
		//does not exist yet)
		//happen concurrently on the same entity group.
        //We catch duplicate key exception in transaction commit.
		//2. Faster retrieval of document using field docname
		coll.createIndex(new BasicDBObject(DOCNAME, 1),
			new BasicDBObject("unique", true));

		if (sharded) {
			//enable sharding
			db = mongoClient.getDB("admin");
			DBObject cmd = new BasicDBObject();
			cmd.put("enableSharding", dbName);
			db.command(cmd);

			cmd = new BasicDBObject();
            cmd.put("shardcollection", dbName + "." + dbName);
			cmd.put("key", new BasicDBObject(DOCNAME, 1));
			int chunk = getIntOpt(PROP_INIT_CHUNK, 0);
			if (chunk > 0) {
				cmd.put("numInitialChunks", chunk);
			}
			db.command(cmd);
		}
	}

	@Override
	public void prepare() {
		showBalancerLock();
		int sleepSec = getIntOpt(PROP_SLEEP_AFTER_SEC, 0);
		if (sleepSec > 0) {
			LOGGER.info("sleeping " + sleepSec + " seconds...");
			sleep(sleepSec);
			LOGGER.info("sleeping done.");
			showBalancerLock();
		}
		DB db = mongoClient.getDB(dbName);
		LOGGER.info("getting DB stats: " + dbName);
		CommandResult res = db.getStats();
		if (res.ok()) {
			LOGGER.info("DB stats OK: " + res);
		} else {
			LOGGER.warn("DB stats NG: " + res);
		}
		DBCollection coll = db.getCollection(dbName);
		LOGGER.info("getting Collection stats: " + dbName);
		res = coll.getStats();
		if (res.ok()) {
			LOGGER.info("Collection stats OK: " + res);
		} else {
			LOGGER.warn("Collection stats NG: " + res);
		}
	}
	void showBalancerLock() {
		for (DBObject o : mongoClient.getDB("config").getCollection("locks").find(
				new BasicDBObject("_id", "balancer"))) {
			LOGGER.info("balancer: " + o);
		}
	}
	void sleep(long sec) {
		if (!Thread.currentThread().isInterrupted()) {
			long sleepMsec = TimeUnit.SECONDS.toMillis(sec);
			try {
				Thread.sleep(sleepMsec);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

		}
	}
}
