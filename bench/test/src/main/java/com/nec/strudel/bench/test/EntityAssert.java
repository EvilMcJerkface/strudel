package com.nec.strudel.bench.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

public final class EntityAssert {

	private EntityAssert() {
	}

	protected static Map<String, String> describe(Object entity) {
		try {
			return BeanUtils.describe(entity);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e); // TODO refactor
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e); // TODO refactor
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e); // TODO refactor
		}
	}

	public static <E> boolean contains(E entity, Collection<E> entities) {
		for (E e : entities) {
			if (describe(entity).equals(describe(e))) {
				return true;
			}
		}
		return false;
	}

	public static <E> void assertSameEntitySets(
			Collection<E> expected, Collection<E> actual) {
		assertEquals(expected.size(), actual.size());
		for (E e : actual) {
			assertContains(e, expected);
		}
	}

	public static <E> void assertContains(E entity, Collection<E> entities) {
		assertTrue(contains(entity, entities));
	}

	public static void assertEntityEquals(Object expected, Object actual) {
		assertNotNull(actual);
		assertEquals(describe(expected), describe(actual));
	}

}
