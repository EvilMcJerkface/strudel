package com.nec.strudel.instrument;

import javax.annotation.concurrent.NotThreadSafe;


@NotThreadSafe
public interface Instrumented<T> {

	T getObject();
	Profiler getProfiler();
}
