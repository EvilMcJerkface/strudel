package com.nec.strudel.tkvs.store.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;
import com.nec.strudel.tkvs.Transaction;
import com.nec.strudel.tkvs.TransactionalDB;

public abstract class TKVStoreTestBase {
	static final AtomicInteger COUNTER = new AtomicInteger();

	public abstract TransactionalDB getDB(String name);

	protected String newName() {
		return "db" + COUNTER.getAndIncrement();
	}

	@Test
	public void testRunTransaction() {
		String dbName = newName();
		String txName = "tx";
		Key tk = Key.create("t1");

		TransactionalDB db = getDB(dbName);
		//assertEquals(dbName, db.getName());

		String collection = "r";
		Record r = Record.create("1","2");
		Key pk = Key.create("k1");
		Transaction tx = db.start(txName, tk);
		assertNotNull(tx);
		//Record r0 = tx.get(collection, pk);
		//assertNull(r0);
		tx.put(collection, pk, r);
		Record r1 = tx.get(collection, pk);
		assertEquals(r, r1);
		boolean success = tx.commit();
		assertTrue(success);

		/**
		 * Transaction update must be visible
		 */
		Transaction tx1 = db.start(txName, tk);
		Record r2 = tx1.get(collection, pk);
		assertEquals(r, r2);
		tx1.commit();

		TransactionalDB db1 = getDB(dbName);
		Transaction tx2 = db1.start(txName, tk);
		Record r3 = tx2.get(collection, pk);
		assertEquals(r, r3);

	}
	@Test
	public void testLargeRecord() {
	       String dbName = newName();
	        String txName = "tx";
	        Key tk = Key.create("t1");

	        TransactionalDB db = getDB(dbName);
	        //assertEquals(dbName, db.getName());

	        String collection = "idx";
	        String[] ptrs = new String[5000];
	        for (int i = 0; i < ptrs.length; i++) {
	            ptrs[i] = "p" + i;
	        }
	        Key pk = Key.create("k1");
	        Record d = Record.create(ptrs);
	        Transaction tx = db.start(txName, tk);
	        assertNotNull(tx);
	        //Record r0 = tx.get(collection, pk);
	        //assertNull(r0);
	        tx.put(collection, pk, d);
	        Record d1 = tx.get(collection, pk);
	        assertEquals(d, d1);
	        boolean success = tx.commit();
	        assertTrue(success);
	        /**
	         * Transaction update must be visible
	         */
	        Transaction tx1 = db.start(txName, tk);
	        Record d2 = tx1.get(collection, pk);
	        assertEquals(d, d2);

	}
	@Test
	public void test2Transactions() {
		String dbName = newName();
		String txName = "tx";
		Key tk = Key.create("t1");

		TransactionalDB db = getDB(dbName);
		//assertEquals(dbName, db.getName());

		String collection = "r";
		Record r = Record.create("1","2");

		Key pk = Key.create("k1");
		Transaction tx = db.start(txName, tk);
		assertNotNull(tx);
		//Record r0 = tx.get(collection, pk);
		//assertNull(r0);
		tx.put(collection, pk, r);
		Record r1 = tx.get(collection, pk);
		assertEquals(r, r1);
		boolean success = tx.commit();
		assertTrue(success);

		//test 2 transactions on the same primary key
		Transaction tx3 = db.start(txName, tk);
		Record r2 = Record.create("2","2");
		tx3.put(collection, pk, r2);
		Record r3 = tx3.get(collection, pk);
		assertEquals(r2, r3);
		success = tx3.commit();
		assertTrue(success);
	}
	@Test
	public void testMultipleWritesTransaction() {
		String dbName = newName();
		String txName = "tx";
		Key tk = Key.create("t1");

		TransactionalDB db = getDB(dbName);

		String collection = "r";
		Record r = Record.create("1","2");
		Record r1 = Record.create("2","2");
		Record r2 = Record.create("3","3");
		Record r3 = Record.create("4","4");
		Record r4 = Record.create("5","5");

		Key pk = Key.create("k1");
		Transaction tx = db.start(txName, tk);
		assertNotNull(tx);
		tx.put(collection, pk, r);
		tx.put(collection, pk, r1);
		tx.put(collection, pk, r2);
		tx.put(collection, pk, r3);
		tx.put(collection, pk, r4);
		Record rr = tx.get(collection, pk);
		assertEquals(rr, r4);
		boolean success = tx.commit();
		assertTrue(success);
	}
	@Test
	public void testDelete() {
		String dbName = newName();
		String txName = "tx";
		Key tk = Key.create("t1");

		TransactionalDB db = getDB(dbName);

		String collection = "r";
		Record r = Record.create("1","2");
		Key pk = Key.create("k1");
		Transaction tx = db.start(txName, tk);
		assertNotNull(tx);
		tx.put(collection, pk, r);
		tx.delete(collection, pk);
		Record rr = tx.get(collection, pk);
		assertEquals(rr, null);
		boolean success = tx.commit();
		assertTrue(success);
	}

}
