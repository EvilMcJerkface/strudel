package com.nec.strudel.bench.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.IndexType;
import com.nec.strudel.tkvs.EntityDBImpl;
import com.nec.strudel.tkvs.impl.inmemory.InMemoryDB;

public abstract class AbstractEntityTestBase {
	private final EntityDB db = new EntityDBImpl(new InMemoryDB("test"));

	protected EntityDB getDb() {
		return db;
	}

	protected <T> void assertSingle(T expected, Iterable<T> it) {
		T actual = assertSingle(it);
		assertEquals(expected, actual);
	}

	protected <T> T assertSingle(Iterable<T> it) {
		Iterator<T> itr = it.iterator();
		assertTrue(itr.hasNext());
		T t = itr.next();
		assertFalse(itr.hasNext());
		return t;
	}

	protected <T> T getNotNull(Class<T> c, Object key) {
		T entity = getDb().get(c, key);
		assertNotNull(entity);
		return entity;
	}

	protected <T> void assertEmpty(Class<T> ec,
			String c, Object key) {
		assertTrue(getList(ec, c, key).isEmpty());
	}
	protected <T> void assertSingle(T e,
			String c, Object key) {
		assertEquals(e, getSingle(e.getClass(), c, key));
	}

	protected <T> T getSingle(Class<T> ec,
			String property, Object key) {
		List<T> list = getList(ec, property, key);
		return assertSingle(list);
	}

	protected <T> List<T> getList(Class<T> ec,
			String property, Object key) {
		return getDb().getEntitiesByIndex(ec, property, key);
	}
	@SuppressWarnings("unchecked")
	protected <T> void assertIndexed(T e, String property) {
		Object key = IndexType.on(e.getClass(), property).getIndexKey(e);
		EntityAssert.assertContains(e, (List<T>) getList(
				e.getClass(), property, key));
	}
	

	protected <T> void assertGetNull(Class<T> c, Object key) {
		T entity = getDb().get(c, key);
		assertNull(entity);
	}
	protected <E> void assertGetEquals(E expected, Class<E> c, Object key) {
		E entity = getDb().get(c, key);
		assertEquals(expected, entity);
	}

	protected <E> E populateNew(Class<E> ec,
			EntityBuilder<E> builder) {
		EntityDB db = getDb();
		E entity = newEntity(ec);
		builder.build(entity, 0);
		db.create(entity);
		return entity;
	}
	protected <E> List<E> populateList(Class<E> ec,
			int size,
			EntityBuilder<E> builder) {
		List<E> list = new ArrayList<E>();
		EntityDB db = getDb();
		for (int i = 0; i < size; i++) {
			E e = newEntity(ec);
			builder.build(e, i);
			db.create(e);
			list.add(e);
		}
		return list;
	}

	<E> E newEntity(Class<E> ec) {
		try {
			return ec.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	protected void populate(Object... objs) {
		EntityDB db = getDb();
		for (Object o : objs) {
			db.create(o);
		}
	}
}
