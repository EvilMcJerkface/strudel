/*******************************************************************************
 *   Copyright 2015 Junichi Tatemura
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package com.nec.strudel.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

public final class EntityUtil {

	private EntityUtil() {
		// not instantiated
	}

	public static Object getProperty(Object entity, String name) {
		try {
			return PropertyUtils.getProperty(entity, name);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e); // TODO refactor
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e); // TODO refactor
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e); // TODO refactor
		}
	}
	public static void setProperty(Object entity,
			String name, Object value) {
		try {
			BeanUtils.setProperty(entity, name, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e); // TODO refactor
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e); // TODO refactor
		}
	}


	public static String toString(Object entity) {
		return describe(entity).toString();
	}
	public static boolean equals(Object entity1, Object entity2) {
		if (entity1 == entity2) {
			return true;
		}
		if (entity1 == null || entity2 == null) {
			return false;
		}
		if (entity1.getClass().equals(entity2.getClass())) {
			return describe(entity1).equals(describe(entity2));
		}
		return false;
	}
	private static final int HASH_BASE = 31;
	public static int hashCode(Object entity) {
		int hashCode = entity.getClass().hashCode();
		hashCode = hashCode * HASH_BASE
				+ describe(entity).hashCode();
		return hashCode;
	}

    public static Map<String, String> describe(Object entity) {
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

}
