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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nec.strudel.entity.EntityUtil;
import com.nec.strudel.entity.info.BeanInfo;

public abstract class KeyFinder {
	public abstract Object getKey(Object entity);

	public static KeyFinder identity(Class<?> keyClass) {
		return new SelfKeyFinder(keyClass);
	}
	public static KeyFinder finderFor(BeanInfo srcInfo,
			Class<?> dstClass,
			List<String> properties) {
		if (srcInfo.getClass().equals(dstClass)) {
			return new SelfKeyFinder(dstClass);
		}
		if (properties.size() == 1) {
			String fname = properties.get(0);
			return new SingleKeyFinder(
					srcInfo.getGetter(fname));
		}
		Map<String, Method> getters = new HashMap<String, Method>();
		for (String fname : properties) {
			getters.put(fname, srcInfo.getGetter(fname));
		}
		return new CompoundKeyFinder(
				dstClass, getters);
	}

	public static KeyFinder propertyFinder(final String name) {
		return new KeyFinder() {

			@Override
			public Object getKey(Object entity) {
				return EntityUtil.getProperty(entity, name);
			}
			
		};
	}
}