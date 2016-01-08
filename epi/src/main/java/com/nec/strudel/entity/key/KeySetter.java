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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.nec.strudel.entity.info.BeanInfo;

public abstract class KeySetter {
	public abstract void setKey(Object entity, Object key);
	public static KeySetter createSetter(BeanInfo src, BeanInfo dst,
			List<String> key) {
		Class<?> keyClass = src.valueClass();
		if (key.size() == 1) {
			Method m = dst.getSetter(key.get(0), keyClass);
			return new DirectSetter(m);
		}
		List<KeySetter> setters =
				new ArrayList<KeySetter>(key.size());
		for (String name : key) {
			Method getter = src.getGetter(name);
			Method setter = dst.getSetter(name,
					getter.getReturnType());
			setters.add(new GetterSetter(getter, setter));
		}
		return new CompoundSetter(setters);
		
	}
	public static KeySetter createSetter(BeanInfo dst, String key) {
		Field f = dst.findField(key);
		if (f == null) {
			throw new RuntimeException("no such field:" + key
					+ " of " + dst.valueClass());
		}
		Method m = dst.findSetter(key, f.getType());
		if (m == null) {
			throw new RuntimeException("no setter for:" + key
					+ " of " + dst.valueClass());
		}
		return new DirectSetter(m);
	}
	static class DirectSetter extends KeySetter {
		private final Method setter;
		public DirectSetter(Method setter) {
			this.setter = setter;
		}
		@Override
		public void setKey(Object entity, Object key) {
			try {
				setter.invoke(entity, key);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}
	static class GetterSetter extends KeySetter {
		private final Method getter;
		private final Method setter;
		public GetterSetter(Method getter, Method setter) {
			this.getter = getter;
			this.setter = setter;
		}

		@Override
		public void setKey(Object entity, Object key) {
			try {
				setter.invoke(entity, getter.invoke(key));
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}
	static class CompoundSetter extends KeySetter {
		private final List<KeySetter> keySetters;
		public CompoundSetter(List<KeySetter> keySetters) {
			super();
			this.keySetters = keySetters;
		}
		@Override
		public void setKey(Object entity, Object key) {
			for (KeySetter setter : keySetters) {
				setter.setKey(entity, key);
			}
		}
	}
}