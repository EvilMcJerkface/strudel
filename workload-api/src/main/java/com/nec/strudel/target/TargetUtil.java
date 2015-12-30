package com.nec.strudel.target;

import com.nec.strudel.instrument.InstrumentUtil;
import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.ProfilerService;

public final class TargetUtil {

	private TargetUtil() {
		// not instantiated
	}
	public static <T> Target<T> sharedTarget(final T obj) {
		return new Target<T>() {

			@Override
			public void close() {
			}

			@Override
			public T open() {
				return obj;
			}

			@Override
			public Instrumented<T> open(
					ProfilerService profs) {
				return InstrumentUtil.uninstrumented(obj);
			}
			@Override
			public void beginUse(T target) {
			}
			@Override
			public void endUse(T target) {
			}
		};
	}
}
