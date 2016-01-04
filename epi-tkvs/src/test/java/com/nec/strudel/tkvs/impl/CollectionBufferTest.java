package com.nec.strudel.tkvs.impl;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;
import com.nec.strudel.tkvs.impl.CollectionBuffer;
import com.nec.strudel.tkvs.impl.CollectionBufferImpl;
import com.nec.strudel.tkvs.impl.KVStore;
import com.nec.strudel.tkvs.impl.TransactionProfiler;
import com.nec.strudel.tkvs.impl.inmemory.InMemoryDB;

public class CollectionBufferTest {

	protected KVStore getKVStore() {
		return new InMemoryDB("test").store("test", Key.create("test"));
	}

	@Test
	public void testRead() {
		KVStore kvs = getKVStore();
		CollectionBuffer buff = new CollectionBufferImpl("r", kvs,
				TransactionProfiler.NO_PROF);
		Key k1 = Key.create("k1");
		Record r1 = Record.create("r1");
		Key k2 = Key.create("k2");
		Record r2 = Record.create("r2");
		Key k3 = Key.create("k3");
//		Record r3 = Record.create("r3");
		buff.load(k1, r1);
		buff.load(k2, r2);
		assertEquals(r1, buff.get(k1));
		assertNull(buff.get(k3));
		Set<Key> reads = buff.getReads();
		assertEquals(2, reads.size());
		assertTrue(reads.contains(k1));
		assertTrue(reads.contains(k3));
		assertFalse(reads.contains(k2));
		assertTrue(buff.getWrites().isEmpty());
	}

	@Test
	public void testWrite() {
		KVStore kvs = getKVStore();
		CollectionBuffer buff = new CollectionBufferImpl("r", kvs,
				TransactionProfiler.NO_PROF);
		Key k1 = Key.create("k1");
		Record r1 = Record.create("r1");
		Key k2 = Key.create("k2");
		Record r2 = Record.create("r2");
		Key k3 = Key.create("k3");
		Record r3 = Record.create("r3");
		buff.load(k1, r1);
		buff.load(k2, r2);
		buff.put(k3, r3);
		buff.delete(k2);
		Record r1a = Record.create("r1a");
		buff.put(k1, r1a);
		assertEquals(r1a, buff.get(k1));
		assertNull(buff.get(k2));
		assertEquals(r3, buff.get(k3));
		Set<Key> reads = buff.getReads();
		assertEquals(3, reads.size());
		Map<Key, Record> writes = buff.getWrites();
		assertEquals(3, writes.size());
		assertTrue(writes.containsKey(k2));
		assertNull(writes.get(k2));
		assertEquals(r3, writes.get(k3));
		assertEquals(r1a, writes.get(k1));
	}
}
