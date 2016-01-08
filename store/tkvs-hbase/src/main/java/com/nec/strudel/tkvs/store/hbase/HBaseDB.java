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

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HConnection;

import com.nec.strudel.entity.IsolationLevel;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.SerDeUtil;
import com.nec.strudel.tkvs.TkvStoreException;
import com.nec.strudel.tkvs.Transaction;
import com.nec.strudel.tkvs.TransactionalDB;
import com.nec.strudel.tkvs.impl.TransactionProfiler;


public class HBaseDB implements TransactionalDB {
	private final String name;
	private final HTableDescriptor tab;
	private final HConnection hconn;
	private final TransactionProfiler prof;
	private final BackoffPolicy backoff;

	public HBaseDB(HConnection hconn, String dbName,
			HTableDescriptor tab,
			TransactionProfiler prof, BackoffPolicy backoff) {
		this.name = dbName;
		this.tab = tab;
		this.hconn = hconn;
		this.prof = prof;
		this.backoff = backoff;
	}
	@Override
	public Transaction start(String groupName, Key groupKey) {
		//rowid: groupName + groupKey
        byte[] rowid = SerDeUtil.toBytes(groupName + groupKey);
		try {
			return new HBaseTransaction(groupName, groupKey, rowid,
                hconn.getTable(tab.getName()), prof);
		} catch (IOException e) {
			throw new TkvStoreException(
					  "Failed to start HBaseTransaction (IOException)", e);
		}
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
    	return backoff;
    }
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void close() { }


}
