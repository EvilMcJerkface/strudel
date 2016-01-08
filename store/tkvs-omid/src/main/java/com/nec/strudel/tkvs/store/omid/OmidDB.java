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

import org.apache.hadoop.conf.Configuration;

import com.nec.strudel.entity.IsolationLevel;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.TkvStoreException;
import com.nec.strudel.tkvs.Transaction;
import com.nec.strudel.tkvs.TransactionalDB;
import com.nec.strudel.tkvs.impl.TransactionProfiler;
import com.yahoo.omid.transaction.HBaseTransactionManager;
import com.yahoo.omid.transaction.TTable;
import com.yahoo.omid.transaction.TransactionException;


public class OmidDB implements TransactionalDB {
	private final HBaseTransactionManager[] tms;
	private final Configuration[] confs;
	private final int numTso;
	private final String name;
	private final TransactionProfiler prof;
	private final BackoffPolicy policy;

	public OmidDB(HBaseTransactionManager[] tms,
			String dbname, Configuration[] confs,
				String[] tsoHosts,
				TransactionProfiler prof,
				BackoffPolicy policy) {
		this.name = dbname;
		this.tms = tms;
		this.confs = confs;
		this.numTso = tsoHosts.length;
		this.prof = prof;
		this.policy = policy;
	}
	@Override
	public Transaction start(String groupName, Key groupKey) {
		HBaseTransactionManager mytm;
		Configuration myconf;
		if (this.numTso == 1) {
			mytm = this.tms[0];
			myconf = this.confs[0];
		} else {
			int tsoIndex =
                Math.abs((groupName + groupKey).hashCode() % this.numTso);
			mytm = this.tms[tsoIndex];
			myconf = this.confs[tsoIndex];
		}
		try {
			return new OmidTransaction(groupName,
				groupKey, name, mytm,
				new TTable(myconf, this.name),
				mytm.begin(), prof);
		} catch (IOException e) {
			throw new TkvStoreException(
					 "Failed to start OmidTransaction (IOException)", e);
		} catch (TransactionException e) {
			throw new TkvStoreException(
					 "Failed to start OmidTransaction (TransactionException)", e);
		}
	}

    @Override
    public Transaction start(String groupName,
            Key groupKey, IsolationLevel level) {
        return start(groupName, groupKey);
    }

    @Override
    public IsolationLevel maxIsolationLevel() {
        return IsolationLevel.SNAPSHOT;
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
