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
import org.apache.hadoop.hbase.HTableDescriptor;

import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.SerDeUtil;
import com.nec.strudel.tkvs.impl.TransactionProfiler;
import com.nec.strudel.tkvs.impl.TransactionalDbServer;
import com.nec.strudel.tkvs.store.hbase.HBaseControl;
import com.yahoo.omid.transaction.HBaseTransactionManager;
import com.yahoo.omid.transaction.OmidInstantiationException;

public class OmidDbServer implements TransactionalDbServer<TransactionProfiler> {
	private final HBaseTransactionManager[] tms;
	private final String dbName;
	private final BackoffPolicy policy;
	private final Configuration[] confs;
	private final int numTso;
	private final String[] tsoHosts;
	public static final byte[] ENTITYCF =
			SerDeUtil.toBytes("ENTITY");
	public OmidDbServer(String dbName, Configuration conf,
			HTableDescriptor tab,
			String[] tsoHosts,
			BackoffPolicy policy) throws IOException, OmidInstantiationException {
		this.dbName = dbName;
		this.tsoHosts = tsoHosts;
		this.policy = policy;
		setUp(conf, tab);
	    this.numTso = this.tsoHosts.length;
	    this.tms = new HBaseTransactionManager[numTso];
	    this.confs = new Configuration[numTso];
	    for (int i = 0; i < numTso; i++) {
	    	confs[i] = new Configuration(conf);
	    	confs[i].set("tso.host", this.tsoHosts[i]);
	    	tms[i] = HBaseTransactionManager.newBuilder()
	    			.withConfiguration(confs[i]).build();
	    }
	}
	public void setUp(Configuration conf, HTableDescriptor tab) throws IOException {
		HBaseControl ctrl = new HBaseControl(conf, tab);
		try {
			ctrl.createTables();
		} finally {
			ctrl.close();
		}
	}
	@Override
	public OmidDB open(TransactionProfiler prof) {
		return new OmidDB(tms, dbName, confs,
				tsoHosts, prof, policy);
	}

	@Override
	public void close() {
		for (int i = 0; i < this.numTso; i++) {
			try {
				tms[i].close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}