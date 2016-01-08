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
package com.nec.strudel.entity.info;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.JoinColumn;

import com.nec.strudel.entity.EntityUtil;

public class BeanInfo implements ValueInfo {
	private final Class<?> entityClass;
	private final List<String> properties;
	private final List<Class<?>> types;
	public BeanInfo(Class<?> entityClass) {
		this.entityClass = entityClass;
		Field[] fields = entityClass.getDeclaredFields();
		List<String> list = new ArrayList<String>();
		List<Class<?>> types = new ArrayList<Class<?>>();
		boolean columnAnnotated = false;
		for (Field f : fields) {
			if (f.isAnnotationPresent(Column.class)) {
				columnAnnotated = true;
				list.add(f.getName());
				types.add(f.getType());
			}
		}
		if (!columnAnnotated) { // when @Column is not specified
			for (Field f : fields) {
				if (isColumnToInclude(f)) {
					list.add(f.getName());
					types.add(f.getType());
				}
			}
		}
		this.properties = Collections.unmodifiableList(list);
		this.types = Collections.unmodifiableList(types);
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}
	@Override
	public Class<?> valueClass() {
		return entityClass;
	}
	public Object[] toTuple(Object bean) {
		Object[] tuple = new Object[properties.size()];
		for(int i = 0; i < tuple.length; i++) {
			tuple[i] = EntityUtil.getProperty(bean,
					properties.get(i));
		}
		return tuple;
	}
	public Object create(Object... tuple) {
		Object bean = newInstance();
		for (int i = 0; i < tuple.length; i++) {
			EntityUtil.setProperty(bean,
					properties.get(i), tuple[i]);
		}
		return bean;
	}
	public Object newInstance() {
		try {
			return entityClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	@Nullable
	public <T extends Annotation> T annotation(Class<T> ann) {
		return entityClass.getAnnotation(ann);
	}
	public Method getGetter(String name) {
		String methodName = "get" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		try {
			return entityClass.getMethod(methodName);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Nullable
	public Method findGetter(String name) {
		String methodName = "get" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		try {
			return entityClass.getMethod(methodName);
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		}
	}
	@Nullable
	public Method findSetter(String name, Class<?> type) {
		String methodName = "set" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		try {
			return entityClass.getMethod(methodName, type);
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		}
	}
	public Method getSetter(String name, Class<?> type) {
		String methodName = "set" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		try {
			return entityClass.getMethod(methodName, type);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	public List<String> properties() {
		return properties;
	}
	public List<Class<?>> types() {
		return types;
	}
	protected boolean isColumnToInclude(Field f) {
		if (Modifier.isStatic(f.getModifiers())) {
			return false;
		}
		/**
		 * Join Columns are ignored
		 */
		if (f.isAnnotationPresent(JoinColumn.class)) {
			return false;
		}
		return true;
	}

	public Class<?> typeOf(String name) {
		Field f = findField(name);
		if (f != null) {
			return f.getType();
		} else {
			return findGetter(name).getReturnType();
		}
	}
	@Nullable
	public Field findField(String name) {
		try {
			return entityClass.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		}
	}
	public List<Field> fields() {
		return Arrays.asList(entityClass.getDeclaredFields());
	}
}