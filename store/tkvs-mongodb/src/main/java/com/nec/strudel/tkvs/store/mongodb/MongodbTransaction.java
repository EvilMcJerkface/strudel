package com.nec.strudel.tkvs.store.mongodb;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.BackoffTime;
import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;
import com.nec.strudel.tkvs.RetryException;
import com.nec.strudel.tkvs.SerDeUtil;
import com.nec.strudel.tkvs.TkvStoreException;
import com.nec.strudel.tkvs.impl.CollectionBuffer;
import com.nec.strudel.tkvs.impl.KVStore;
import com.nec.strudel.tkvs.impl.TransactionBaseImpl;
import com.nec.strudel.tkvs.impl.TransactionProfiler;
import com.nec.strudel.tkvs.store.mongodb.MongodbStore.MongoDbServer;

public class MongodbTransaction extends TransactionBaseImpl {
	private static final Logger LOGGER = Logger.getLogger(
			MongodbTransaction.class);
	public static final long INIT_WAIT = 1000;
	public static final int MONGO_RETRY = 16;
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
	private final DBCollection coll;
	private final String gName;
	private final String docName;
	private final Long vnum;
	private final TransactionProfiler prof;


	public MongodbTransaction(String gName,
			Key gKey, DBCollection coll,
			TransactionProfiler prof) {
		super(gName, gKey, new MongodbKVStore(coll,
				gName + gKey), prof);
		this.coll = coll;
		this.docName = gName + gKey;
		this.vnum = getVnum(coll, docName);
    	this.gName = gName;
    	this.prof = prof;
	}

	static long getVnum(DBCollection coll, String docName) {
		while (true) {
			BackoffTime bot = MONGO_BACKOFF.newBackoff();
			try {
				return tryGetVnum(coll, docName);
			} catch (MongoException e) {
				long wait = bot.failed();
				if (wait < 0) {
					throw e;
				} else if (wait > 0) {
					LOGGER.warn("Mongo operation failed. retrying: "
							+ e.getMessage());
					try {
						Thread.sleep(wait);
					} catch (InterruptedException e1) {
						throw e;
					}
				}
			}
		}
		
	}
	static long tryGetVnum(DBCollection coll, String docName) {
		DBCursor cur =
	            coll.find(new BasicDBObject(MongoDbServer.DOCNAME, docName),
	            new BasicDBObject(MongoDbServer.VERSIONFIELD, 1)).limit(1);
			DBObject versionObj = null;
			if (cur.hasNext()) {
				versionObj = cur.next();
			}
			if (versionObj != null) {
				return Long.parseLong(versionObj.get(MongoDbServer
						.VERSIONFIELD).toString());
			} else {
				return 0L;
			}
		
	}
	
	@Override
	public boolean commit() {
		prof.commitStart(gName);
		BasicDBObject setdoc = new BasicDBObject();
		BasicDBObject unsetdoc = new BasicDBObject();
		for (CollectionBuffer b : buffers()) {
			Map<Key, Record> writes = b.getWrites();
			String name = b.getName();
			for (Map.Entry<Key, Record> e : writes.entrySet()) {
				Key key = e.getKey();
				Record r = e.getValue();
				//for put
				if (r != null) {
					setdoc.append(name + key,
						SerDeUtil.toBytes(r));
				} else { // for delete
					unsetdoc.append(name + key, "");
				}
			}
		}
		if (setdoc.isEmpty() && unsetdoc.isEmpty()) {
			//read only
			long newvnum = getVnum(coll, docName);
			if (this.vnum == newvnum) {
				prof.commitSuccess(gName);
				return true;
			} else {
				prof.commitFail(gName);
				return false;
			}
		} else {
			setdoc.append(MongoDbServer.VERSIONFIELD, vnum + 1);
			WriteResult res = null;
			if (vnum == 0) {
				//upsert if this docName is not in the db yet,
                //else we should receive a duplicateKey exception, meaning
                //this docName is inserted by an other transaction, then fail it
				if (unsetdoc.isEmpty()) {
					res =
							insert(new BasicDBObject(MongoDbServer.DOCNAME,
									docName),
									new BasicDBObject("$set", setdoc));
				} else {
					res =
							insert(new BasicDBObject(MongoDbServer.DOCNAME,
									docName),
									new BasicDBObject("$set", setdoc)
							.append("$unset", unsetdoc));
				}
				if (res == null) {
					prof.commitFail(gName);
					return false;
				}
				prof.commitSuccess(gName);
				return true;
			} else {
				if (unsetdoc.isEmpty()) {
					res =
                        update(new BasicDBObject(MongoDbServer.DOCNAME,
                            docName).append(MongoDbServer.VERSIONFIELD,
								this.vnum),
                            new BasicDBObject("$set", setdoc));
				} else {
					res =
                        update(new BasicDBObject(MongoDbServer.DOCNAME,
                            docName).append(MongoDbServer.VERSIONFIELD,
								this.vnum),
                            new BasicDBObject("$set",
                                setdoc).append("$unset", unsetdoc));
				}
				if (res.isUpdateOfExisting()) {
					prof.commitSuccess(gName);
					return true;
				} else {
					prof.commitFail(gName);
					return false;
				}
			}
		}
	}
	WriteResult update(DBObject q, DBObject o) {
		try {
			return tryUpdate(q, o);
		} catch (InterruptedException e) {
			throw new TkvStoreException("failed to update", e);
		} catch (RetryException e) {
			throw new TkvStoreException("failed to update", e);
		}
	}
	/**
	 * @return null if it fails due to duplicate
	 * exception (TODO does it happen for upsert?)
	 */
	WriteResult insert(DBObject q, DBObject o) {
		try {
			return tryInsert(q, o);
		} catch (InterruptedException e) {
			throw new TkvStoreException("failed to update", e);
		} catch (RetryException e) {
			if (e.isRetryExpired()) {
				throw new TkvStoreException("retying update failed", e);
			}
			throw new TkvStoreException("failed to update", e.getCause());
		}
	}
	WriteResult tryUpdate(final DBObject q, final DBObject o)
			throws InterruptedException, RetryException {
		return MONGO_BACKOFF.call(new Callable<WriteResult>() {
			@Override
			public WriteResult call() throws Exception {
				return coll.update(q, o);
			}
			
		}, MongoException.class);
	}

	WriteResult tryInsert(final DBObject q, final DBObject o)
			throws InterruptedException, RetryException {
		return MONGO_BACKOFF.call(new Callable<WriteResult>() {
			@Override
			public WriteResult call() throws Exception {
				//upsert if this docName is not in the db yet,
                //else we should receive a duplicateKey exception, meaning
                //this docName is inserted by an other transaction, then fail it
				try {
					return coll.update(q, o, true, false);
				} catch (@SuppressWarnings("deprecation")
				MongoException.DuplicateKey e) {
					return null;
				}
			}
		}, MongoException.class);
	}
	static class MongodbKVStore implements KVStore {
		private final DBCollection coll;
		private final String docName;
		MongodbKVStore(DBCollection coll, String docName) {
			this.coll = coll;
			this.docName = docName;
		}
		@Override
		public Record get(String name, Key key) {
			DBObject obj = get(name, new BasicDBObject(name + key, 1));
			if (obj != null && obj.containsField(name + key)) {
                return SerDeUtil.parseRecord((byte[]) obj.get(name + key));
			} else {
				return null;
			}
		}
		DBObject get(String name, DBObject key) {
			BackoffTime bot = MONGO_BACKOFF.newBackoff();
			while (true) {
				try {
					return tryGet(name, key);
				} catch (MongoException e) {
					LOGGER.warn("Mongo find op failed. retrying: "
							+ e.getMessage());
					long wait = bot.failed();
					if (wait < 0) {
						throw e;
					} else if (wait > 0) {
						try {
							Thread.sleep(wait);
						} catch (InterruptedException e1) {
							throw e;
						}
					}
				}
			}
		}
		DBObject tryGet(String name, DBObject key) {
			DBCursor cur =
					coll.find(new BasicDBObject(MongoDbServer.DOCNAME,
							docName), key).limit(1);
			if (cur.hasNext()) {
				return cur.next();
			}
			return null;
		}
	}
}

