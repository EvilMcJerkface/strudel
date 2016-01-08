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
package com.nec.strudel.bench.micro.populate.jpa;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.bench.micro.populate.SubmitSet;
import com.nec.strudel.bench.micro.populate.jpa.PopulateItem;
import com.nec.strudel.bench.micro.populate.jpa.PopulatePost;
import com.nec.strudel.bench.micro.populate.jpa.PopulateShared;
import com.nec.strudel.bench.micro.populate.jpa.PopulateSubmission;
import com.nec.strudel.bench.test.JpaDatabase;
import com.nec.strudel.workload.api.ValidateReporter;

/**
 * Testing population with JPA. By default it uses Derby.
 * To test with MySQL:
 * <ol>
 * <li> make MySQL available for user=root, password="" and create database "micro"
 * <li> do: mvn test -DargLine="-Dtest.mysql=localhost" or specify a remote hostname.
 * <li> for a different user or password use: test.mysql.user and test.mysql.password,
 * respectively
 * </ol>
 */
public class PopulateTest {
	static final JpaDatabase JPA_DB = new JpaDatabase("micro");
	@BeforeClass
	public static void startup() {
		JPA_DB.startup();
	}
	@AfterClass
	public static void shutdown() {
		JPA_DB.close();
	}
	@Test
	public void testPopulateItem() {
		EntityManager em = JPA_DB.createEntityManager();
		try {
			PopulateItem pop = new PopulateItem();
			ContentSet param = new ContentSet(0, new String[]{"abc", "def"});
			pop.process(em, param);
			boolean valid = pop.validate(em, param, new ValidateReporter() {
				@Override
				public void error(String message) {
					System.err.println(message)	;
					
				}
			});
			assertTrue(valid);
		} finally {
			em.close();
		}
	}
	@Test
	public void testPopulateShared() {
		EntityManager em = JPA_DB.createEntityManager();
		try {
			PopulateShared pop = new PopulateShared();
			ContentSet param = new ContentSet(0, new String[]{"abc", "def"});
			pop.process(em, param);
			boolean valid = pop.validate(em, param, new ValidateReporter() {
				@Override
				public void error(String message) {
					System.err.println(message)	;
					
				}
			});
			assertTrue(valid);
		} finally {
			em.close();
		}
	}
	@Test
	public void testPopulatePost() {
		EntityManager em = JPA_DB.createEntityManager();
		try {
			PopulatePost pop = new PopulatePost();
			ContentSet param = new ContentSet(0, new String[]{"abc", "def"});
			pop.process(em, param);
			boolean valid = pop.validate(em, param, new ValidateReporter() {
				@Override
				public void error(String message) {
					System.err.println(message)	;
					
				}
			});
			assertTrue(valid);
		} finally {
			em.close();
		}
	}
	@Test
	public void testPopulateSubmission() {
		EntityManager em = JPA_DB.createEntityManager();
		try {
			PopulateSubmission pop = new PopulateSubmission();
			SubmitSet param = new SubmitSet(0,
					new int[]{1,2,2,3},
					new String[]{"a","a","b","ccc"});
			pop.process(em, param);
			boolean valid = pop.validate(em, param, new ValidateReporter() {
				@Override
				public void error(String message) {
					System.err.println(message)	;
					
				}
			});
			assertTrue(valid);
		} finally {
			em.close();
		}
		
	}
}
