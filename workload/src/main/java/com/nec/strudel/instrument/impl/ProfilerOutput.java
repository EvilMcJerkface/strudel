package com.nec.strudel.instrument.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nec.strudel.instrument.Profiling;
import com.nec.strudel.json.func.Value;

public final class ProfilerOutput {

	private ProfilerOutput() {
	}

	public static List<NamedFunc> outputsOf(Class<?> profiler) {
		List<NamedFunc> funcs = new ArrayList<NamedFunc>();
		ProfilerDescriptor desc = ProfilerDescriptor.of(profiler);
		for (InstrumentDescriptor instr : desc.getInstruments()) {
			switch (instr.getType()) {
			case COUNT:
				funcs.add(new NamedFunc(instr.getName(), Value.of(instr.getName())));
				break;
			case TIME:
				funcs.addAll(TimeOutput.outputsOf(instr.getName()));
				break;
			default:
				throw new IllegalArgumentException(
						"unsupported type: " + instr.getType());
			}
		}
		return funcs;
	}

	public static List<NamedFunc> on(Class<?> profiled) {
		Profiling prof = profiled.getAnnotation(Profiling.class);
		if (prof != null) {
			return outputsOf(prof.value());
		}
		Class<?> sup = profiled.getSuperclass();
		if (sup != null) {
			return on(sup);
		}
		return Collections.emptyList();
	}

}
