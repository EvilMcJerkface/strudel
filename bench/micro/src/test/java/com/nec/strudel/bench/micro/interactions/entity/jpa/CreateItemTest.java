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
package com.nec.strudel.bench.micro.interactions.entity.jpa;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.nec.strudel.bench.micro.interactions.entity.CreateItem;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.jpa.EntityDBImpl;

@TestOn(CreateItem.class)
public class CreateItemTest extends com.nec.strudel.bench.micro.interactions.CreateItemTest {
	private static DBTestStore store;
	private EntityDBImpl db;
	@BeforeClass
	public static void startup() {
		store = new DBTestStore();

	}
	@AfterClass
	public static void shutdown() {
		if (store != null) {
			store.close();
		}
	}
	@Before
	public void begin() {
		db = store.open();
	}
	@After
	public void end() {
		if (db != null) {
			db.close();
		}
	}

	@Override
	protected EntityDB getDb() {
		return db;
	}

}
