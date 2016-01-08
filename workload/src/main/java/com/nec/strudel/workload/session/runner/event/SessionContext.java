package com.nec.strudel.workload.session.runner.event;

import org.apache.log4j.Logger;

import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.Profiler;
import com.nec.strudel.instrument.impl.ProfilerUtil;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.Result.Warn;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.exec.Report;
import com.nec.strudel.workload.session.SessionProfiler;
import com.nec.strudel.workload.util.WarningReporter;

public class SessionContext<T> {
	private static final Logger LOGGER = Logger.getLogger(SessionContext.class);
    private static final int WARN_MAX = 20;
    private final Instrumented<? extends SessionProfiler> profile;
    private final Target<T> target;
    private final Instrumented<T> con;
    private final WarningReporter warn;
    private final Profiler profiler;

	public SessionContext(Target<T> target,
			Instrumented<T> con,
			Instrumented<? extends SessionProfiler> profile) {
		this.target = target;
		this.con = con;
		this.profile = profile;
		this.warn = new WarningReporter(WARN_MAX, LOGGER);
		this.profiler = ProfilerUtil.union(profile.getProfiler(), con.getProfiler());
	}

	public T getConnection() {
		return con.getObject();
	}
	public SessionProfiler profiler() {
		return profile.getObject();
	}
	public Target<T> target() {
		return target;
	}
	public Report getReport() {
		return Report.report(profiler.getValue(), warn.report());
	}

	public void inspectResult(String name, Result r) {
        if (r.isSuccess()) {
            LOGGER.debug("done: "
                    + name
                    + (r.hasMode() ? ":" + r.getMode() : ""));
        } else {
            LOGGER.debug("failed: "
                    + name
                    + (r.hasMode() ? ":" + r.getMode() : ""));
        }
        if (r.hasWarning()) {
        	for (Warn w : r.getWarnings()) {
                warn.warn(name + ":" + w.getMessage());
        	}
        }
	}

}
