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
package com.nec.strudel.entity.info;

import java.util.Date;

public final class ValueTypes {
	private static final Class<?>[] PRIMITIVES = {
		Number.class,
		String.class,
		Byte.class,
		Boolean.class,
		Character.class,
		Date.class,
		java.util.Calendar.class,
		byte[].class,
		Byte[].class,
		char[].class,
		Character[].class,
		java.sql.Date.class,
		java.sql.Time.class,
		java.sql.Timestamp.class,
	};
	private ValueTypes() {
	}

	public static boolean isPrimitive(Class<?> valueClass) {
		if (valueClass.isPrimitive()) {
			return true;
		}
		for (Class<?> pc : PRIMITIVES) {
			if (pc.isAssignableFrom(valueClass)) {
				return true;
			}
		}
		return false;
	}
	public static ValueInfo infoOf(Class<?> valueClass) {
		if (isPrimitive(valueClass)) {
			return new PrimitiveValueInfo(valueClass);
		} else {
			return new BeanInfo(valueClass);
		}
	}
	public static class PrimitiveValueInfo implements ValueInfo {
		private final Class<?> valueClass;
		public PrimitiveValueInfo(Class<?> valueClass) {
			this.valueClass = valueClass;
		}
		@Override
		public Class<?> valueClass() {
			return valueClass;
		}

		@Override
		public boolean isPrimitive() {
			return true;
		}
		
	}
}
