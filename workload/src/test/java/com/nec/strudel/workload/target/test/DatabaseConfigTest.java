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
package com.nec.strudel.workload.target.test;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

import com.nec.strudel.target.Target;
import com.nec.strudel.target.TargetConfig;
import com.nec.strudel.target.impl.DatabaseConfig;
import com.nec.strudel.target.impl.TargetFactory;
import com.nec.strudel.tkvs.TransactionalDB;
import com.nec.strudel.tkvs.impl.inmemory.InMemoryDB;
import com.nec.strudel.tkvs.store.impl.InMemoryStore;
import com.nec.strudel.workload.test.DBFiles;
import com.nec.strudel.workload.test.Resources;
import com.nec.strudel.workload.test.kvmap.KVMap;

public class DatabaseConfigTest {

	@Test
	public void testDatabaseConfig() {
		TargetConfig conf = Resources.create(DBFiles.DB_TKVS);
		assertEquals(InMemoryStore.class.getName(),
		        conf.getClassName());
		assertEquals("auction", conf.getName());
		assertEquals("tkvs", conf.getType());
		Properties props = conf.getProperties();
		final int size = 3;
		assertEquals(size, props.size());
		for (int i = 1; i <= size; i++) {
			assertEquals("v" + i, props.get("k" + i));
		}
		Target<TransactionalDB> store = TargetFactory.create(conf);
		TransactionalDB db = store.open();
		assertEquals("auction", db.getName());
		assertTrue(db instanceof InMemoryDB);
	}

	@Test
	public void testCustomDBCreator() {
		DatabaseConfig conf =  Resources.create(DBFiles.DB_TEST);
		Target<KVMap> store = TargetFactory.create(conf);
		KVMap kvMap = store.open();
		assertEquals("test", kvMap.getName());
	}
}
