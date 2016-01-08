package com.nec.strudel.instrument.impl;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import com.nec.strudel.instrument.Instrument;
import com.nec.strudel.instrument.Profiler;

public class InstrumentDescriptor {
	public enum Type {
		TIME,
		COUNT
	}
	private final String name;
	private final Type type;
	public InstrumentDescriptor(String name, Type type) {
		this.name = name;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public Type getType() {
		return type;
	}
	public String toString() {
		return name + ":" + type;
	}
	public Profiler createInstrument(ProfilerServiceImpl ps, Object stat) {
		if (type == Type.TIME) {
			return ProfilerFactory.timeProfiler(name, ps, stat);
		} else {
			return new CountingProfiler(name,
					ps.getMeasurementState());
		}
	}
	public Profiler createInstrument(ProfilerServiceImpl ps) {
		if (type == Type.TIME) {
			return ProfilerFactory.timeProfiler(name, ps);
		} else {
			return new CountingProfiler(name,
					ps.getMeasurementState());
		}
	}
	public Profiler createDisabled() {
		if (type == Type.TIME) {
			return TimeProfiler.NO_TIME;
		} else {
			return CountingProfiler.NO_COUNT;
		}
	}
	@Nullable
	public static InstrumentDescriptor of(Field f) {
		Instrument v = f.getAnnotation(Instrument.class);
		if (v != null) {
			String name = v.name().isEmpty() ? f.getName() : v.name();
			return new InstrumentDescriptor(name, getType(f));
		}
		return null;
	}
	private static Type getType(Field f) {
		Class<?> cls = f.getType();
		if (cls.isAssignableFrom(TimeProfiler.class)) {
			return Type.TIME;
		}
		if (cls.isAssignableFrom(CountingProfiler.class)) {
			return Type.COUNT;
		}
		throw new IllegalArgumentException("missing type on " + f.getName());
	}
}