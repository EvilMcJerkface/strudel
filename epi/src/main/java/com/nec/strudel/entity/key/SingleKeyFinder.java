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
package com.nec.strudel.entity.key;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SingleKeyFinder extends KeyFinder {
	private final Method getter;
	private final Class<?> valueClass;
	public SingleKeyFinder(Method getter) {
		this.getter = getter;
		this.valueClass = getter.getDeclaringClass();
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
			return getter.invoke(entity);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}