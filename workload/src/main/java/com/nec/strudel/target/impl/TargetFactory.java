package com.nec.strudel.target.impl;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.instrument.impl.NamedFunc;
import com.nec.strudel.instrument.impl.ProfilerOutput;
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
