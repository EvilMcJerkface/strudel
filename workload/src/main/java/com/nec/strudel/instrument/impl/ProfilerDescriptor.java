package com.nec.strudel.instrument.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nec.strudel.instrument.Profiler;

public class ProfilerDescriptor {
	public static ProfilerDescriptor of(Class<?> profilerClass) {
		return new ProfilerDescriptor(profilerClass);
	}

	private final Class<?> profilerClass;
	private final List<InstrumentDescriptor> instruments =
			new ArrayList<InstrumentDescriptor>();
	private final Map<String, Method> instrumentSetters = new HashMap<String, Method>();
	private final Map<String, Method> instrumentGetters = new HashMap<String, Method>();

	public ProfilerDescriptor(Class<?> profilerClass) {
		this.profilerClass = profilerClass;
		create(profilerClass, instruments, instrumentSetters,
				instrumentGetters);
	}

	public Collection<InstrumentDescriptor> getInstruments() {
		return instruments;
	}

	@SuppressWarnings("unchecked")
	public <T> T create(ProfilerServiceImpl ps, Object stat) {
		try {
			Object prof = this.profilerClass.newInstance();
			setInstruments(prof, ps, stat);
			return (T) prof;
		} catch (InstantiationException e) {
			throw new RuntimeException("failed to create profiler", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("failed to create profiler", e);
		}
	}
	@SuppressWarnings("unchecked")
	public <T> T create(ProfilerServiceImpl ps) {
		try {
			Object prof = this.profilerClass.newInstance();
			setInstruments(prof, ps);
			return (T) prof;
		} catch (InstantiationException e) {
			throw new RuntimeException("failed to create profiler", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("failed to create profiler", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T createDisabled() {
		try {
			Object prof = this.profilerClass.newInstance();
			setDisabled(prof);
			return (T) prof;
		} catch (InstantiationException e) {
			throw new RuntimeException("failed to create profiler", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("failed to create profiler", e);
		}
	}
	public Profiler extractInstrument(Object profiler) {
		List<Profiler> profs = new ArrayList<Profiler>();
		for (InstrumentDescriptor instr : getInstruments()) {
			Profiler p = getInstrument(instr.getName(), profiler);
			profs.add(p);
		}
		return profs.size() == 1 ? profs.get(0) : new CompoundProfiler(profs);
	}

	public void setInstruments(Object profiler, ProfilerServiceImpl ps, Object stat) {
		for (InstrumentDescriptor instr : getInstruments()) {
			Profiler in = instr.createInstrument(ps, stat);
			setInstrument(instr.getName(), profiler, in);
		}
	}
	public void setInstruments(Object profiler, ProfilerServiceImpl ps) {
		for (InstrumentDescriptor instr : getInstruments()) {
			Profiler in = instr.createInstrument(ps);
			setInstrument(instr.getName(), profiler, in);
		}
	}
	public void setDisabled(Object profiler) {
		for (InstrumentDescriptor instr : getInstruments()) {
			Profiler in = instr.createDisabled();
			setInstrument(instr.getName(), profiler, in);
		}
	}

	private static void create(Class<?> profilerClass,
			List<InstrumentDescriptor> instruments,
			Map<String, Method> instrumentSetters,
			Map<String, Method> instrumentGetters) {
		Class<?> sup = profilerClass.getSuperclass();
		if (sup != null) {
			create(sup, instruments, instrumentSetters, instrumentGetters);
		}
		for (Field f : profilerClass.getDeclaredFields()) {
			InstrumentDescriptor instr = InstrumentDescriptor.of(f);
			if (instr != null) {
				instruments.add(instr);
				Method s = findSetter(profilerClass, f.getName(), f.getType());
				if (s != null) {
					instrumentSetters.put(instr.getName(),  s);
				}
				Method g = findGetter(profilerClass, f.getName(), f.getType());
				if (g != null) {
					instrumentGetters.put(instr.getName(), g);
				}
			}
		}

	}

	private static Method findSetter(Class<?> prof, String name, Class<?> type) {
		String methodName = "set" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		try {
			return prof.getMethod(methodName, type);
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		}
	}
	private static Method findGetter(Class<?> prof, String name, Class<?> type) {
		String methodName = "get" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		try {
			Method m = prof.getMethod(methodName);
			if (m.getReturnType().equals(type)) {
				return m;
			}
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}
		return null;
	}

	private void setInstrument(String name, Object prof, Object instr) {
		Method s = instrumentSetters.get(name);
		try {
			s.invoke(prof, instr);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("failed to set instrument:" + name, e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("failed to set instrument:" + name, e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("failed to set instrument:" + name, e);
		}
		
	}
	private Profiler getInstrument(String name, Object prof) {
		Method g = instrumentGetters.get(name);
		try {
			return (Profiler) g.invoke(prof);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("failed to get instrument:" + name, e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("failed to get instrument:" + name, e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("failed to get instrument:" + name, e);
		}
	}
}