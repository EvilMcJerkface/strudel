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

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.nec.strudel.entity.jpa.EntityDbImpl;

public class DBTestStore {
	private final EntityManagerFactory emf;
	public static final String DBPATH = "/tmp/testDb";

	public DBTestStore() {
		Properties props = new Properties();
		props.setProperty("javax.persistence.jdbc.url", 
				"jdbc:derby:" + DBPATH + ";create=true");
		props.setProperty("javax.persistence.schema-generation.database.action",
				"drop-and-create");
		emf = Persistence.createEntityManagerFactory("micro", props);
	}

	public void close() {
		emf.close();
	}
	public EntityDbImpl open() {
		EntityManager em = emf.createEntityManager();
		return new EntityDbImpl(em);
	}
}
