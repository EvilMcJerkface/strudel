package com.nec.strudel.bench.micro.interactions.entity.jpa;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.nec.strudel.bench.micro.interactions.entity.GetPost;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.jpa.EntityDBImpl;

@TestOn(GetPost.class)
public class GetPostTest extends com.nec.strudel.bench.micro.interactions.GetPostTest {
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