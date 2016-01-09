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

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;
import com.nec.strudel.tkvs.RetryException;
import com.nec.strudel.tkvs.SerDeUtil;
import com.nec.strudel.tkvs.TkvStoreException;
import com.nec.strudel.tkvs.impl.CollectionBuffer;
import com.nec.strudel.tkvs.impl.KVStore;
import com.nec.strudel.tkvs.impl.TransactionBaseImpl;
import com.nec.strudel.tkvs.impl.TransactionProfiler;
import com.nec.strudel.tkvs.store.tokumx.TokumxStore.TokumxDbServer;

/**
 *
 * @author tatemura, Zheng Li (initial version)
 *
 */
public class TokumxTransaction extends TransactionBaseImpl {
	private static final Logger LOGGER = Logger.getLogger(TokumxTransaction.class);
	public static final long INIT_WAIT = 1000;
	public static final int MONGO_RETRY = 10;
	public static final long MAX_WAIT_SEC = 10;
	public static final long MAX_TOTAL_SEC = 30;
	public static final BackoffPolicy MONGO_BACKOFF =
			BackoffPolicy.builder()
			.maxTrial(MONGO_RETRY)
			.initWaitMS(INIT_WAIT)
			.startBackoff(0) // backoff from the first retry
			.maxWaitMS(TimeUnit.SECONDS.toMillis(MAX_WAIT_SEC))
			.maxTotalMS(TimeUnit.SECONDS.toMillis(MAX_TOTAL_SEC))
			.build();
	private final BulkWriteOperation bulkwriter;
	//private final DBCollection coll;
	private final DB db;
	private final String gName;
	private final TransactionProfiler prof;
	private final MongodbKVStore kvs;
	public TokumxTransaction(String gName, Key gKey,
			String dbname, DB db, DBCollection coll,
			TransactionProfiler prof) {
		this(gName, gKey, dbname, db, new MongodbKVStore(coll), coll, prof);
	}
	public TokumxTransaction(String gName, Key gKey,
			String dbname, DB db, MongodbKVStore kvs, DBCollection coll,
			TransactionProfiler prof) {
		super(gName, gKey, kvs, prof);
		db.requestStart();
		this.db = db;
		//this.coll = coll;
		this.kvs = kvs;
    	this.gName = gName;
    	this.prof = prof;
    	this.bulkwriter = coll.initializeOrderedBulkOperation();


    	DBObject cmd =
    		new BasicDBObject("beginTransaction", 1).append("isolation",
    				"serializable");
    	//Default is MVCC snapshot isolation level.
//    	DBObject cmd = new BasicDBObject("beginTransaction", 1);
    	CommandResult res = runCommand(db, cmd);
    	if (!res.ok()) {
    		LOGGER.warn("failed to begin transaction: " + res.getErrorMessage());
    	}
	}
	@SuppressWarnings("deprecation")
	CommandResult runCommand(final DB db, final DBObject cmd) {
		try {
			return MONGO_BACKOFF.call(new Callable<CommandResult>() {
				@Override
				public CommandResult call() throws Exception {
					return db.command(cmd);
				}
			}, MongoException.Network.class);
		} catch (InterruptedException e) {
			throw new TkvStoreException(
				"Tokumx Transaction failed", e);
		} catch (RetryException e) {
			throw new TkvStoreException(
					"Tokumx Transaction failed", e);
		}
	}

	@Override
	public boolean commit() {
		prof.commitStart(gName);
		if (kvs.isAborted()) {
			prof.commitFail(gName);
			return false;
		}
		boolean noop = true;
		for (CollectionBuffer b : buffers()) {
			Map<Key, Record> writes = b.getWrites();
			String name = b.getName();
			for (Map.Entry<Key, Record> e : writes.entrySet()) {
				Key key = e.getKey();
				Record r = e.getValue();
				String docName = name + key;
				//for put
				if (r != null) {
                    //bulk or update? Tested: almost the same performance
					if (noop) {
						noop = false;
					}
                    bulkwriter.find(new BasicDBObject(TokumxDbServer.DOCNAME,
						docName)).upsert().update(
						new BasicDBObject("$set",
                            new BasicDBObject(TokumxDbServer.VALUENAME,
                                SerDeUtil.toBytes(r))));
//					coll.update(new
//                      BasicDBObject(TokumxDbServer.docname, docName),
//						new BasicDBObject("$set",
//	                    new BasicDBObject(TokumxDbServer.valuename,
//	                    SerDeUtil.toBytes(r))),
//							true,
//							false);

				} else { // for delete
					if (noop) {
						noop = false;
					}
                    bulkwriter.find(new BasicDBObject(TokumxDbServer.DOCNAME,
						docName)).remove();
                    //coll.remove(new BasicDBObject(
                    //TokumxDbServer.docname, docName));
				}
			}
		}
		if (!noop) {
			try {
				bulkwriter.execute();
			} catch (com.mongodb.BulkWriteException e) {
				//This is tested to happen when
				//running multi-row transactions,
				//error message is: lock can not be granted
				LOGGER.warn("transaction failed due to BulkWriteException: "
						+ e.getMessage());
				prof.commitFail(gName);
				return false;
			} catch (@SuppressWarnings("deprecation") MongoException.Network e) {
				LOGGER.warn("transaction failed due to mongo network exception: "
						+ e.getMessage());
				prof.commitFail(gName);
				return false;
			} catch (MongoException e) {
				LOGGER.warn("transaction failed due to mongo exception: "
						+ e.getMessage());
				prof.commitFail(gName);
				return false;
				
			}
		}

		DBObject cmd = new BasicDBObject("commitTransaction", 1);
		CommandResult res = runCommand(db, cmd);
		if (res.ok()) {
			prof.commitSuccess(gName);
			db.requestDone();
			return true;
		} else {
			prof.commitFail(gName);
			LOGGER.info("commit failed :" + res.getErrorMessage());
			cmd = new BasicDBObject("rollbackTransaction", 1);
			runCommand(db, cmd);
	    	db.requestDone();
			return false;
		}
	}

	static class MongodbKVStore implements KVStore {
		private final DBCollection coll;
		private boolean aborted = false;
		MongodbKVStore(DBCollection coll) {
			this.coll = coll;
		}
		@SuppressWarnings("deprecation")
		@Override
		public Record get(String name, Key key) {
			final DBObject objkey = new BasicDBObject(TokumxDbServer.DOCNAME,
					name + key);
			DBObject obj;
			try {
				obj = MONGO_BACKOFF.call(
						new Callable<DBObject>() {
							@Override
							public DBObject call() throws Exception {
								return get(objkey);
							}
						},
						MongoException.Network.class
						);
			} catch (InterruptedException e) {
				throw new TkvStoreException("get failed", e);
			} catch (RetryException e) {
				if (!e.isRetryExpired()) {
					/**
					 * failed by exception other than
					 * MongoException.Network
					 */
					Throwable t = e.getCause();
					if (t instanceof MongoException) {
						/**
						 * It is often the case that it failed to
						 * acquire the lock -> transaction should fail.
						 */
						LOGGER.warn("get failed (transaction will abort: "
								+ t.getMessage());
						aborted = true;
						return null;
						
					}
				}
				throw new TkvStoreException("get failed", e);
			}

			if (obj != null && obj.
				containsField(TokumxDbServer.VALUENAME)) {
				return SerDeUtil.parseRecord((byte[]) obj
					.get(TokumxDbServer.VALUENAME));
			} else {
				return null;
			}
		}
		DBObject get(DBObject key) {
			DBCursor cur = coll.find(key).limit(1);
			if (cur.hasNext()) {
				return cur.next();
			} else {
				return null;
			}

		}
		public boolean isAborted() {
			return aborted;
		}
	}
}

