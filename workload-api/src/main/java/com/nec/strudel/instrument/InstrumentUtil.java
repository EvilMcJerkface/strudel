package com.nec.strudel.instrument;

import javax.json.Json;
import javax.json.JsonObject;


public final class InstrumentUtil {

	private InstrumentUtil() {
	}

	public static <T> Instrumented<T> profiled(T obj, Profiler prof) {
		return new ProfiledObject<T>(obj, prof);
	}

	public static <T> Instrumented<T> uninstrumented(final T obj) {
		return new Instrumented<T>() {

			@Override
			public T getObject() {
				return obj;
			}
			@Override
			public Profiler getProfiler() {
				return NO_PROF;
			}
		};
	}
	private static final Profiler NO_PROF = new Profiler() {
		private final JsonObject empty = Json.createObjectBuilder().build();
		@Override
		public JsonObject getValue() {
			return empty;
		}
		
	};
	static class ProfiledObject<T> implements Instrumented<T> {
		private final T con;
		private final Profiler prof;
		public ProfiledObject(T con,
				Profiler prof) {
			this.con = con;
			this.prof = prof;
		}
		@Override
		public T getObject() {
			return con;
		}
		@Override
		public Profiler getProfiler() {
			return prof;
		}

	}
}
