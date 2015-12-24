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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;


public final class EntityGroup {
	private EntityGroup() {
		// not instantiated
	}


	public static boolean isRoot(Class<?> c) {
		Group g = c.getAnnotation(Group.class);
		if (g != null) {
			return g.parent() == Object.class;
		} else {
			/**
			 * No group specification: default
			 * is that each class is a root.
			 */
			return true;
		}
	}


	public static EntityDescriptor descriptor(Class<?> entityClass) {
		EntityDescriptor desc = DESCS.get(entityClass);
		if (desc == null) {
			desc = new EntityDescriptor(entityClass);
			DESCS.putIfAbsent(entityClass, desc);
		}
		
		return desc;
	}

	private static final ConcurrentHashMap<Class<?>, EntityDescriptor> DESCS =
			new ConcurrentHashMap<Class<?>, EntityDescriptor>();


	public static List<String> requiredKeyProperties(Class<?> entityClass) {
		List<String> keys = new ArrayList<String>();
		for (Field f : entityClass.getDeclaredFields()) {
			if (f.isAnnotationPresent(Id.class)) {
				if (!f.isAnnotationPresent(GeneratedValue.class)) {
					keys.add(f.getName());
				}
			}
		}
		return keys;
	}

}
