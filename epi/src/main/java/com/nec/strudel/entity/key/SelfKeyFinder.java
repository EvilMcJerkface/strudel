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


public class SelfKeyFinder extends KeyFinder {
	private final Class<?> valueClass;
	public SelfKeyFinder(Class<?> valueClass) {
		if (valueClass.isPrimitive() || Number.class.isAssignableFrom(valueClass)) {
			/**
			 * TODO we need a special type check
			 * to accept both int and Integer, for example.
			 */
			this.valueClass = Object.class;
		} else {
			this.valueClass = valueClass;
		}
	}
	@Override
	public Object getKey(Object pkey) {
		if (!valueClass.isInstance(pkey)) {
			throw new IllegalArgumentException(
			"invalid class given ("
			+ pkey.getClass().getName() + ")"
			+ " expected:" + valueClass);
		}
		return pkey;
	}
	
}