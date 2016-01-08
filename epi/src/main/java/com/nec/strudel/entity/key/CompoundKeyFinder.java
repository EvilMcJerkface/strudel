/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
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
package com.nec.strudel.entity.key;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

public class CompoundKeyFinder extends KeyFinder {
	private final Map<String, Method> getters;
	private final Class<?> idClass;
	private final Class<?> valueClass;
	public CompoundKeyFinder(Class<?> idClass,
			Map<String, Method> getters) {
		this.idClass = idClass;
		this.getters = getters;
		this.valueClass = getters.values()
				.iterator().next().getDeclaringClass();
	}
	@Override
	public Object getKey(Object entity) {
		if (!valueClass.isInstance(entity)) {
			throw new IllegalArgumentException(
			"invalid class given ("
			+ entity.getClass().getName() + ")"
			+ " expected:" + valueClass);
		}
		try {
			Object id = idClass.newInstance();
			for (Map.Entry<String, Method> en : getters.entrySet()) {
				Object value = en.getValue().invoke(entity);
				BeanUtils.setProperty(id, en.getKey(), value);
			}
			return id;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
}