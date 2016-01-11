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
package com.nec.strudel.target.impl;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.instrument.impl.ProfilerOutput;
import com.nec.strudel.metrics.NamedFunc;
import com.nec.strudel.target.FactoryClass;
import com.nec.strudel.target.Target;
import com.nec.strudel.target.TargetConfig;
import com.nec.strudel.target.TargetCreator;
import com.nec.strudel.target.TargetLifecycle;
import com.nec.strudel.util.ClassUtil;

public final class TargetFactory {
	private TargetFactory() {
		// not instantiated
	}

	public static <T> Target<T> create(TargetConfig dbConfig) {
		TargetCreator<T> dbc = creator(dbConfig);
		return dbc.create(dbConfig);
	}
	public static TargetLifecycle lifecycleManager(TargetConfig config) {
		return creator(config).createLifecycle(config);
	}
	public static List<NamedFunc> outputs(TargetConfig dbConfig) {
		Class<?> instrumented = creator(dbConfig).instrumentedClass(dbConfig);
		if (instrumented != null) {
			return ProfilerOutput.on(instrumented);
		} else {
			return Collections.emptyList();
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> TargetCreator<T> creator(TargetConfig dbConfig) {
		Class<?> c = ClassUtil.forName(dbConfig.getClassName(),
				dbConfig.targetClassLoader());
		if (ClassUtil.isSubclass(c, TargetCreator.class)) {
			return (TargetCreator<T>) ClassUtil.create(
					c.asSubclass(TargetCreator.class));
		}
		FactoryClass ca = findAnnotation(c, FactoryClass.class);
		if (ca != null) {
			return (TargetCreator<T>) ClassUtil.create(ca.value());
		}
		throw new ConfigException("creator not found for "
		+ dbConfig.getClassName());
	}

	static <A extends Annotation> A findAnnotation(
			Class<?> cls, Class<A> a) {
		A ann = cls.getAnnotation(a);
		if (ann != null) {
			return ann;
		}
		for (Class<?> itf : cls.getInterfaces()) {
			ann = itf.getAnnotation(a);
			if (ann != null) {
				return ann;
			}
		}
		return null;
	}
}
