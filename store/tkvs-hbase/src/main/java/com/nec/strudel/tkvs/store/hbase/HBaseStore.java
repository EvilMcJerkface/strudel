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
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;

import com.nec.strudel.target.TargetLifecycle;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.SerDeUtil;
import com.nec.strudel.tkvs.TkvStoreException;
import com.nec.strudel.tkvs.impl.TransactionProfiler;
import com.nec.strudel.tkvs.impl.TransactionalDbServer;
import com.nec.strudel.tkvs.store.TransactionalStore;
import com.nec.strudel.tkvs.store.impl.AbstractTransactionalStore;

public class HBaseStore extends AbstractTransactionalStore implements TransactionalStore {
	@Override
	public HBaseDbServer createDbServer(String dbName, Properties props) {
		Configuration conf = HBaseUtil.config(props);
		try {
			return new HBaseDbServer(conf, dbName,
					tableDescriptor(dbName),
					new BackoffPolicy(props));
		} catch (ZooKeeperConnectionException e) {
			throw new TkvStoreException(
			  "HBaseStore failed to create (ZooKeeper connection)", e);
		} catch (IOException e) {
			throw new TkvStoreException(
					  "HBaseStore failed to create (IOException)", e);
		}
	}

	@Override
	public TargetLifecycle lifecycle(String dbName, Properties props) {
		return new HBaseLifecycle(
				HBaseUtil.config(props),
				tableDescriptor(dbName));
	}

	/**
	 * Creates a universal table for each database
	 * @param dbName
	 */
	public static HTableDescriptor tableDescriptor(String dbName) {
		TableName tablename = TableName.valueOf(dbName);
		HTableDescriptor tableDescriptor =
				new HTableDescriptor(tablename);
		tableDescriptor.addFamily(
			new HColumnDescriptor("VERSION"));
		tableDescriptor.addFamily(
			new HColumnDescriptor("ENTITY"));
		return tableDescriptor;
	}
    public static final byte[] VERSIONCF = SerDeUtil.toBytes("VERSION");
    public static final byte[] VERQUALIFIER = SerDeUtil.toBytes("VNUM");
    public static final byte[] ENTITYCF = SerDeUtil.toBytes("ENTITY");

	public static class HBaseDbServer implements TransactionalDbServer<TransactionProfiler> {
		private final HConnection hconn;
		private final String dbName;
		private final HTableDescriptor tab;
		private final BackoffPolicy backoff;
        public HBaseDbServer(Configuration conf, String dbName,
        		HTableDescriptor tab,
        		BackoffPolicy backoff) throws IOException {
			this.dbName = dbName;
        	this.tab = tab;
        	this.backoff = backoff;
			hconn = HConnectionManager.createConnection(conf);
			
			if (!hconn.isTableAvailable(tab.getTableName())) {
				HBaseControl ctrl = new HBaseControl(conf, tab);
				try {
					ctrl.createTables();
				} finally {
					ctrl.close();
				}
			}
		}

        @Override
        public HBaseDB open(TransactionProfiler prof) {
        	return new HBaseDB(this.hconn, this.dbName,
        			this.tab, prof, backoff);
        }
		@Override
		public void close() {
			try {
				this.hconn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
