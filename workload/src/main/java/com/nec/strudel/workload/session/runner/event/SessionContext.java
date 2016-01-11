/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nec.strudel.workload.session.runner.event;

import org.apache.log4j.Logger;

import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.Profiler;
import com.nec.strudel.instrument.impl.ProfilerUtil;
import com.nec.strudel.metrics.Report;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.Result.Warn;
import com.nec.strudel.target.Target;
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
