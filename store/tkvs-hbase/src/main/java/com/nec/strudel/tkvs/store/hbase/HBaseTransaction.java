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
package com.nec.strudel.tkvs.store.hbase;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.common.primitives.Longs;
import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;
import com.nec.strudel.tkvs.SerDeUtil;
import com.nec.strudel.tkvs.impl.CollectionBuffer;
import com.nec.strudel.tkvs.impl.KVStore;
import com.nec.strudel.tkvs.impl.TransactionBaseImpl;
import com.nec.strudel.tkvs.impl.TransactionProfiler;

public class HBaseTransaction extends TransactionBaseImpl {
	private final HTableInterface htable;
	private final long vnum;
	private final String gName;
	private final byte[] rowid;
	private final TransactionProfiler prof;

	public HBaseTransaction(String gName, Key gKey, byte[] rowid,
			HTableInterface table, TransactionProfiler prof)
				throws IOException {
		super(gName, gKey, new HBaseKVStore(table, rowid), prof);
    	this.rowid = rowid;
		//Get version number
    	Get get = new Get(rowid);
    	get.addColumn(HBaseStore.VERSIONCF, HBaseStore.VERQUALIFIER);
    	Result res = table.get(get);
    	this.vnum = res.isEmpty() ? 0L : Longs.fromByteArray(res.value());
    	this.htable = table;

    	this.gName = gName;
    	this.prof = prof;
	}

	@Override
	public boolean commit() {
		prof.commitStart(gName);
		//Will only change only Row rowid because
		//one entity group is packed in one row
		Put put = new Put(rowid);
		Delete del = new Delete(rowid);
		for (CollectionBuffer b : buffers()) {
			Map<Key, Record> writes = b.getWrites();
			//collection name is different from group name
			String name = b.getName();
			for (Map.Entry<Key, Record> e : writes.entrySet()) {
				Record r = e.getValue();
				//for put
				if (r != null) {
					put.add(HBaseStore.ENTITYCF,
                         SerDeUtil.toBytes(name + e.getKey()),
							SerDeUtil.toBytes(r));
				} else { // for delete
					del.deleteColumn(HBaseStore.ENTITYCF,
                        SerDeUtil.toBytes(name + e.getKey()));
				}
			}
		}
		//do batch commit
        byte[] oldVnumBytes = vnum == 0 ? null : Bytes.toBytes(this.vnum);
		if (put.isEmpty() && del.isEmpty()) {
            //read only transaction, fail commit if version is changed
			Get get = new Get(rowid);
	    	get.addColumn(HBaseStore.VERSIONCF,
	    			HBaseStore.VERQUALIFIER);
	    	Result res = null;
			try {
				res = htable.get(get);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			closeTable();
			if (!res.isEmpty()) {
				long newvnum =
					Longs.fromByteArray(res.value());
				if (newvnum == this.vnum) {
					prof.commitSuccess(gName);
					return true;
				} else {
					prof.commitFail(gName);
					return false;
				}
			} else {
				if (this.vnum == 0L) {
					prof.commitSuccess(gName);
					return true;
				} else {
					prof.commitFail(gName);
					return false;
				}
			}
		} else {
			if (!put.isEmpty() && !del.isEmpty()) {
				try {
					put.add(HBaseStore.VERSIONCF,
							HBaseStore.VERQUALIFIER,
						Bytes.toBytes(vnum + 1));
					if (!htable.checkAndPut(this.rowid,
							HBaseStore.VERSIONCF,
							HBaseStore.VERQUALIFIER,
							oldVnumBytes, put)) {
						prof.commitFail(gName);
						closeTable();
						return false;
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					if (!htable.checkAndDelete(this.rowid,
							HBaseStore.VERSIONCF,
							HBaseStore.VERQUALIFIER,
                        Bytes.toBytes(vnum + 1), del)) {
						prof.commitFail(gName);
						closeTable();
						return false;
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else if (del.isEmpty()) {
				try {
					put.add(HBaseStore.VERSIONCF,
							HBaseStore.VERQUALIFIER,
						Bytes.toBytes(vnum + 1));
					if (!htable.checkAndPut(this.rowid,
							HBaseStore.VERSIONCF,
							HBaseStore.VERQUALIFIER,
							oldVnumBytes, put)) {
						prof.commitFail(gName);
						closeTable();
						return false;
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				//put is empty and del is not empty
				try {
					if (!htable.checkAndDelete(this.rowid,
							HBaseStore.VERSIONCF,
							HBaseStore.VERQUALIFIER,
							oldVnumBytes, del)) {
						prof.commitFail(gName);
						closeTable();
						return false;
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		prof.commitSuccess(gName);
		closeTable();
		return true;
	}

	private void closeTable() {
		try {
			this.htable.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static class HBaseKVStore implements KVStore {
		private final HTableInterface htable;
		private final byte[] rowid;
		HBaseKVStore(HTableInterface htable, byte[] rowid) {
			this.htable = htable;
			this.rowid = rowid;
		}
		@Override
		public Record get(String name, Key key) {
			Get get = new Get(rowid);
			//Do we need to check version here?
			//Or we'll just wait until commit
			get.addColumn(HBaseStore.ENTITYCF,
				SerDeUtil.toBytes(name + key));
			byte[] value = null;
			try {
				value = this.htable.get(get).value();
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
