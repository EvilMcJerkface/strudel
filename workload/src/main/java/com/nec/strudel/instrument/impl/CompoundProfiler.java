package com.nec.strudel.instrument.impl;

import java.util.List;

import javax.json.JsonObject;

import com.nec.strudel.instrument.Profiler;
import com.nec.strudel.json.JsonValues;

public class CompoundProfiler implements Profiler {

	private final List<Profiler> profilers;

	public CompoundProfiler(List<Profiler> profilers) {
		this.profilers = profilers;
	}

	@Override
	public JsonObject getValue() {
		JsonObject[] values = new JsonObject[profilers.size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = profilers.get(i).getValue();
		}
		return JsonValues.union(values);
	}

}
