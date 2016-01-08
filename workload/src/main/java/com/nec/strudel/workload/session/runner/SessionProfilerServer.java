package com.nec.strudel.workload.session.runner;

import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.workload.session.SessionProfiler;

public interface SessionProfilerServer {

	Instrumented<? extends SessionProfiler> profiler();
}
