package com.nec.strudel.instrument.impl;

import java.util.List;

import javax.json.JsonObject;

import com.nec.strudel.instrument.Profiler;
import com.nec.strudel.json.JsonValues;

public final class ProfilerUtil {

	private ProfilerUtil() {
	}

	public static Profiler union(final Profiler... profs) {
		return new Profiler() {

			@Override
			public JsonObject getValue() {
				JsonObject[] values = new JsonObject[profs.length];
				for (int i = 0; i < profs.length; i++) {
					values[i] = profs[i].getValue();
				}
				return JsonValues.union(values);
			}
		};
	}
	public static Profiler union(List<Profiler> profs) {
		return new CompoundProfiler(profs);
	}

}
