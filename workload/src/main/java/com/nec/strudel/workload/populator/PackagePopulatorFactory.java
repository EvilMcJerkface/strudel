/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
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
package com.nec.strudel.workload.populator;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import com.nec.strudel.util.ClassUtil;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.PopulatorFactory;

public class PackagePopulatorFactory<T> implements PopulatorFactory<T> {
	private static final Logger LOGGER =
			Logger.getLogger(PackagePopulatorFactory.class);
	private final Map<String, Populator<T, ?>> pops =
			new HashMap<String, Populator<T, ?>>();

	public PackagePopulatorFactory(String packageName, String classPath) {
		Reflections refs = new Reflections(
				packageName,
				ClassUtil.loaderFor(classPath));
		@SuppressWarnings("rawtypes")
		Set<Class<? extends Populator>> clss =
				refs.getSubTypesOf(Populator.class);
		for (@SuppressWarnings("rawtypes")
			Class<? extends Populator> c : clss) {
			if (!Modifier.isAbstract(c.getModifiers())) {
				@SuppressWarnings("unchecked")
				Populator<T, ?> intr = ClassUtil.create(c);
				pops.put(intr.getName(), intr);
			}
		}
		if (pops.isEmpty()) {
			LOGGER.warn("no populator found for package " + packageName
					+ " in " + classPath);
		} else {
			int size = pops.size();
			LOGGER.info(size + " populator" + (size > 1 ? "s" : "")
					+ " found for " + packageName);
		}
	}

	@Override
	public Populator<T, ?> create(String name) {
		return pops.get(name);
	}

}
