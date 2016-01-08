package com.nec.strudel.workload.session;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.InteractionFactory;
import com.nec.strudel.util.ClassUtil;

/**
 * Implementation of an interaction factory that
 * creates all the interaction implementations in a specified
 * package.
 * @author tatemura
 *
 * @param <T> type of the database on which an interaction works
 */
public class PackageInteractionFactory<T> implements InteractionFactory<T> {
	private static final Logger LOGGER = Logger.getLogger(PackageInteractionFactory.class);

	private final Map<String, Interaction<T>> interactionMap =
			new HashMap<String, Interaction<T>>();

	public PackageInteractionFactory(String packageName, String classPath) {
		Reflections refs = new Reflections(
				packageName, ClassUtil.loaderFor(classPath));
		@SuppressWarnings("rawtypes")
		Set<Class<? extends Interaction>> clss =
				refs.getSubTypesOf(Interaction.class);
		for (@SuppressWarnings("rawtypes")
			Class<? extends Interaction> c : clss) {
			if (!Modifier.isAbstract(c.getModifiers())) {
				@SuppressWarnings("unchecked")
				Interaction<T> intr = ClassUtil.create(c);
				interactionMap.put(FactoryUtil.getName(intr), intr);
			}
		}
		if (interactionMap.isEmpty()) {
			LOGGER.warn("no interaction found for package " + packageName
					+ " in " + classPath);
		} else {
			int size = interactionMap.size();
			LOGGER.info(size + " interaction" + (size > 1 ? "s" : "")
					+ " found for " + packageName);
		}
	}

	@Override
	public Interaction<T> create(String name) {
		return interactionMap.get(name);
	}
	@Override
	public Set<String> names() {
		return Collections.unmodifiableSet(interactionMap.keySet());
	}

}
