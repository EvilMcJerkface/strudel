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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.nec.strudel.target.TargetLifecycle;
import com.nec.strudel.tkvs.BackoffPolicy;
import com.nec.strudel.tkvs.TkvStoreException;
import com.nec.strudel.tkvs.impl.TransactionProfiler;
import com.nec.strudel.tkvs.impl.TransactionalDbServer;
import com.nec.strudel.tkvs.store.TransactionalStore;
import com.nec.strudel.tkvs.store.impl.AbstractTransactionalStore;

/**
 * property:
 * <ul>
 * <li> mongodb.mongos: server addresses to mongos
 * (e.g., "example1:27017,example2:27017",
 * "example1,example2,example3", "localhost")
 * The store uses the local one if it is in the list.
 * Otherwise, it randomly chooses one in the list.
 * </ul>
 * @author tatemura, Zheng Li (initial version)
 *
 */
public class MongodbStore extends AbstractTransactionalStore implements TransactionalStore {
	private static final Logger LOGGER =
		    Logger.getLogger(MongodbStore.class);
	@Override
	public MongoDbServer createDbServer(String dbName, Properties props) {
		ServerAddress addr = chooseOne(getServerAddressList(props));
		LOGGER.info("using mongos@"
				+ addr.getHost() + ":" + addr.getPort());
		try {
			return new MongoDbServer(dbName, addr,
					new BackoffPolicy(props));
		} catch (IOException e) {
			throw new TkvStoreException(
					  "MongodbStore failed to create (IOException)", e);
		}
	}
	ServerAddress chooseOne(List<ServerAddress> addrs) {
		if (addrs.size() == 1) {
			return addrs.get(0);
		}
		InetAddress local = thisHost();
		String host = local.getHostName();
		for (ServerAddress a : addrs) {
			if (a.sameHost("localhost")) {
				return a;
			} else if (a.sameHost(host)) {
				return a;
			} else {
				try {
					InetAddress addr = InetAddress.getByName(
							a.getHost());
					if (local.equals(addr)) {
						return a;
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Random rand = new Random();
		return addrs.get(rand.nextInt(addrs.size()));
	}
	InetAddress thisHost() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
	List<ServerAddress> getServerAddressList(Properties props) {
		String[] mongos = props.getProperty("mongodb.mongos", "localhost").split(",");
		List<ServerAddress> list = new ArrayList<ServerAddress>();
		for (String m : mongos) {
			String[] parts = m.split(":");
			try {
				if (parts.length == 2) {
					list.add(new ServerAddress(parts[0],
							Integer.parseInt(parts[1])));
				} else {
					list.add(new ServerAddress(m));
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	@Override
	public TargetLifecycle lifecycle(String dbName, Properties props) {
		List<ServerAddress> list = getServerAddressList(props);
		boolean sharded = list.size() > 1;
		ServerAddress addr = chooseOne(list);
		return new MongodbLifecycle(dbName, addr, sharded, props);
	}


	public static class MongoDbServer implements TransactionalDbServer<TransactionProfiler> {
		private final MongoClient mongoClient;
		private final DBCollection coll;
		private final String dbName;
		private final BackoffPolicy policy;
		public static final String VERSIONFIELD = "!version!";
		public static final String DOCNAME = "!group + key!";
		public MongoDbServer(String dbName, String host) throws UnknownHostException, IOException {
			this(dbName, new ServerAddress(host), new BackoffPolicy());
		}
		MongoDbServer(String dbName, ServerAddress host,
				BackoffPolicy policy) throws IOException {
			this.dbName = dbName;
			this.policy = policy;
			this.mongoClient = new MongoClient(host);
			DB db = this.mongoClient.getDB(dbName);
			this.coll = db.getCollection(dbName);
		}

		@Override
		public MongodbDB open(TransactionProfiler prof) {
			return new MongodbDB(dbName, coll,
					prof, policy);
		}
		@Override
		public void close() {
			this.mongoClient.close();
		}
	}
}
