package com.nec.strudel.instrument.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.NotThreadSafe;
import javax.json.Json;
import javax.json.JsonObject;

import com.nec.strudel.instrument.OperationListener;
import com.nec.strudel.instrument.Profiler;
import com.nec.strudel.instrument.TimeInstrument;
import com.nec.strudel.json.JsonValues;

@NotThreadSafe
public class TimeProfiler implements Profiler, TimeInstrument {
	private final CountingProfiler count;
	private final CountingProfiler time;
	private final MeasurementState measure;
	private final OperationListener listener;
	private String current = "";
	private long start;
	public static TimeOutput output(String name) {
		return new TimeOutput(name);
	}

	public TimeProfiler(String name, MeasurementState measure) {
		this(name, measure, NO_LISTENER);
	}
	public TimeProfiler(String name,
			MeasurementState measure, OperationListener listener) {
		this.count = new CountingProfiler(TimeOutput.countOf(name));
		this.time = new CountingProfiler(TimeOutput.timeOf(name));
		this.measure = measure;
		this.listener = listener;
	}
	@Override
	public void start(String name) {
		current = name;
		start = System.nanoTime();
	}
	@Override
	public long end() {
		if (start == 0) {
			return 0;
		}
		long micro =
				TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - start);
		if (measure.isMeasuring()) {
			time.add(current, micro);
			count.increment(current);
		}
		listener.operation(micro);
		start = 0;
		return micro;
	}

	@Override
	public JsonObject getValue() {
		return JsonValues.union(
				count.getValue(),
				time.getValue());
	}

	private static final OperationListener NO_LISTENER =
			new OperationListener() {
				@Override
				public void operation(long microSec) {
				}
	};
	public static final NoTime NO_TIME = new NoTime();
	public static class NoTime implements Profiler, TimeInstrument {
		private final JsonObject empty =
				Json.createObjectBuilder().build();
		@Override
		public void start(String name) {
		}

		@Override
		public long end() {
			return 0;
		}

		@Override
		public JsonObject getValue() {
			return empty;
		}
		
	}

}
