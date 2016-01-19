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
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;

import com.nec.strudel.target.TargetLifecycle;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.TkvStoreException;
import com.nec.strudel.tkvs.store.TransactionalStore;
import com.nec.strudel.tkvs.store.hbase.HBaseLifecycle;
import com.nec.strudel.tkvs.store.hbase.HBaseUtil;
import com.nec.strudel.tkvs.store.impl.AbstractTransactionalStore;
import com.yahoo.omid.transaction.OmidInstantiationException;

/**
 * Omid-based implementation of TransactionalDB
 * <ul>
 * <li> tso.host (localhost) - a CSV of Omid hosts
 * <li> tso.port  (1234)
 * <li> hbase.zookeeper.quorum (localhost) - a CSV of zookeeper hosts
 * <li> hbase.zookeeper.property.clientPort (2181)
 * </ul>
 * @author tatemura, Zheng Li (initial version)
 *
 */
public class OmidStore extends AbstractTransactionalStore implements TransactionalStore {
	private static final int TSOPORT = 1234;
	private static final int CLIENTPORT = 2181;

	@Override
	public OmidDbServer createDbServer(String dbName, Properties props) {
		/**
		 * NOTE without this, HBaseConfiguration fails to find
		 * hbase-default.xml
		 */
		Thread.currentThread().setContextClassLoader(
				HBaseConfiguration.class.getClassLoader());
		Configuration conf = HBaseConfiguration.create();
		conf.set("tso.host", "localhost");
		conf.setInt("tso.port", TSOPORT);
		conf.set("hbase.zookeeper.quorum", "localhost");
		conf.setInt("hbase.zookeeper.property.clientPort", CLIENTPORT);
        //Max Transactions that omid server will handle at the same time,
		//This won't be in effect here, to set this parameter.
		//modify the value in omid.sh
		//conf.setInt("omid.maxItems", 1);
		String[] tsoHosts = null;
		for (Map.Entry<Object, Object> e : props.entrySet()) {
            conf.set(e.getKey().toString(), e.getValue().toString());
			if (e.getKey().toString().equals("tso.host")) {
				tsoHosts = e.getValue().toString().split(",");
			}
		}
		try {
			return new OmidDbServer(dbName, conf,
					tableDescriptor(dbName), tsoHosts,
					new BackoffPolicy(props));
		} catch (ZooKeeperConnectionException e) {
			throw new TkvStoreException(
					  "OmidStore failed to create (ZooKeeper connection)", e);
		} catch (IOException e) {
			throw new TkvStoreException(
					  "OmidStore failed to create (IOException)", e);
		} catch (OmidInstantiationException e) {
			throw new TkvStoreException(
					  "OmidStore failed to create (Omid instanitation)", e);
		}
	}
	@Override
	public TargetLifecycle lifecycle(String dbName, Properties props) {
		return new HBaseLifecycle(HBaseUtil.config(props),
				tableDescriptor(dbName));
	}

	public HTableDescriptor tableDescriptor(String dbName) {
		TableName tablename = TableName.valueOf(dbName);
		HTableDescriptor desc =
				new HTableDescriptor(tablename);
		HColumnDescriptor datafam =
				new HColumnDescriptor("ENTITY");
		desc.addFamily(datafam);
		return desc;
	}
}
