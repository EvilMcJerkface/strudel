package com.nec.strudel.instrument.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;
import javax.json.Json;
import javax.json.JsonObject;

import com.nec.strudel.instrument.CountInstrument;
import com.nec.strudel.instrument.Profiler;

@NotThreadSafe
public class CountingProfiler implements Profiler, CountInstrument {
	private final Map<String, Long> counts =
			new HashMap<String, Long>();
	private final String name;
	private final MeasurementState measure;
	public CountingProfiler(String name) {
		this.name = name;
		this.measure = MeasurementState.ALWAYS;
	}
	public CountingProfiler(String name, MeasurementState measure) {
		this.name = name;
		this.measure = measure;
	}

	@Override
	public void increment(String name) {
		add(name, 1);
	}
	@Override
	public void add(String name, long value) {
		if (measure.isMeasuring()) {
			Long v = counts.get(name);
			if (v != null) {
				v += value;
				counts.put(name, v);
			} else {
				counts.put(name, value);
			}
		}
	}

	@Override
	public JsonObject getValue() {
		return ProfilerValue.builder(name)
				.set(counts).build();
	}
	public static NoCount NO_COUNT = new NoCount();

	public static class NoCount implements Profiler, CountInstrument {
		private final JsonObject empty =
				Json.createObjectBuilder().build();
		@Override
		public void increment(String name) {
		}

		@Override
		public void add(String name, long value) {
		}

		@Override
		public JsonObject getValue() {
			return empty;
		}
		
	}
}
