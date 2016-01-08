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
package com.nec.strudel.tkvs.store.omid;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;

import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;
import com.nec.strudel.tkvs.SerDeUtil;
import com.nec.strudel.tkvs.impl.CollectionBuffer;
import com.nec.strudel.tkvs.impl.KVStore;
import com.nec.strudel.tkvs.impl.TransactionBaseImpl;
import com.nec.strudel.tkvs.impl.TransactionProfiler;
import com.yahoo.omid.transaction.HBaseTransactionManager;
import com.yahoo.omid.transaction.RollbackException;
import com.yahoo.omid.transaction.TTable;
import com.yahoo.omid.transaction.Transaction;
import com.yahoo.omid.transaction.TransactionException;

public class OmidTransaction extends TransactionBaseImpl {
	private final HBaseTransactionManager tm;
	//Have to use one TTable per transaction because the
	//underlying HTable is not thread safe
	private final TTable tt;
	private final Transaction tx;
	private final String gName;
	private final String dbName;
	private final TransactionProfiler prof;

	public OmidTransaction(String gName, Key gKey,
			String dbName, HBaseTransactionManager tm, TTable tt,
			Transaction tx, TransactionProfiler prof)
				throws IOException, TransactionException {
		super(gName, gKey, new OmidKVStore(tt, tx, dbName), prof);
		this.tm = tm;
		this.tt = tt;
		this.dbName = dbName;
		this.tx = tx;
    	this.gName = gName;
    	this.prof = prof;
	}

	@Override
	public boolean commit() {
		prof.commitStart(gName);
		Put put;
		Delete del;
		for (CollectionBuffer b : buffers()) {
			Map<Key, Record> writes = b.getWrites();
			String name = b.getName();
			for (Map.Entry<Key, Record> e : writes.entrySet()) {
				Key key = e.getKey();
				Record r = e.getValue();
                //rowid : dbName + collection name + key in collection
                byte[] rowid = SerDeUtil.toBytes(dbName
                		+ name + key.toString());
				//for put
				if (r != null) {
					try {
						put = new Put(rowid);
						put.add(OmidDbServer.ENTITYCF,
                                OmidDbServer.ENTITYCF,
                                SerDeUtil.toBytes(r));
						tt.put(tx, put);
					} catch (IllegalArgumentException e1) {
                        // TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
                        // TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else { // for delete
					try {
						del = new Delete(rowid);
                        del.deleteColumn(OmidDbServer.ENTITYCF,
                            OmidDbServer.ENTITYCF);
						tt.delete(tx, del);
					} catch (IOException e1) {
                        // TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		//commit
			try {
				tm.commit(tx);
			} catch (RollbackException e) {
				prof.commitFail(gName);
				return false;
			} catch (TransactionException e) {
				prof.commitFail(gName);
				return false;
			} finally {
				try {
					tt.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		prof.commitSuccess(gName);
		return true;
	}

	static class OmidKVStore implements KVStore {
		private final TTable tt;
		private final Transaction tx;
		private final String dbName;
		OmidKVStore(TTable tt, Transaction tx, String dbName) {
			this.tt = tt;
			this.tx = tx;
			this.dbName = dbName;
		}
		@Override
		public Record get(String name, Key key) {
			Get get = new Get(SerDeUtil.toBytes(dbName
					+ name + key.toString()));
			get.addColumn(OmidDbServer.ENTITYCF,
					OmidDbServer.ENTITYCF);
			byte[] value = null;
			try {
				value = tt.get(tx, get).value();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (value != null) {
				return SerDeUtil.parseRecord(value);
			} else {
				return null;
			}
		}
	}
}

