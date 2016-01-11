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
package com.nec.strudel.workload.session.runner;

import org.apache.log4j.Logger;

import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.Profiler;
import com.nec.strudel.instrument.impl.ProfilerUtil;
import com.nec.strudel.metrics.Report;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.Result.Warn;
import com.nec.strudel.session.impl.State;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.exec.batch.WorkThread;
import com.nec.strudel.workload.session.SessionContainer;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.session.SessionProfiler;
import com.nec.strudel.workload.session.StateFactory;
import com.nec.strudel.workload.util.WarningReporter;

public abstract class AbstractSessionRunner<T>
implements WorkThread, RunnerStat {
    private static final Logger LOGGER =
            Logger.getLogger(AbstractSessionRunner.class);
    private static final int WARN_MAX = 20;

    private final SessionFactory<T> sfactory;
    private final StateFactory states;
    private final int id;
    private volatile boolean running;
    private volatile boolean done = false;
    private volatile boolean success = false;
    private final Instrumented<? extends SessionProfiler> sessionProf;
    private final Instrumented<T> con;
    private final Target<T> target;
    private final WarningReporter warn;
    private final Profiler profiler;
    public AbstractSessionRunner(int id,
            SessionFactory<T> sfactory,
            Instrumented<T> con,
            Target<T> target,
            StateFactory states,
            Instrumented<? extends SessionProfiler> prof) {
        this.id = id;
        this.sfactory = sfactory;
        this.states = states;
        this.con = con;
        this.target = target;
        this.sessionProf = prof;
        this.warn = new WarningReporter(WARN_MAX, LOGGER);
        this.profiler = ProfilerUtil.union(prof.getProfiler(),
        		con.getProfiler());
    }
    @Override
    public int getId() {
    	return id;
    }

    @Override
    public Report getReport() {
    	return Report.report(profiler.getValue(),
    			warn.report());
    }

    @Override
    public void stop() {
    	running = false;
    	synchronized (this) {
    		this.notifyAll();
    	}
    }
	@Override
	public final void run() {
	    running = true;
	    try {
		    runSessions();
		    success = true;
	    } finally {
	    	running = false;
	    	done = true;
	    }
	}
	@Override
	public boolean isDone() {
		return done;
	}

	protected void inspectResult(String name, Result r) {
        if (r.isSuccess()) {
            LOGGER.debug("done: "
                    + name
                    + (r.hasMode() ? ":" + r.getMode() : "")
                    + "@" + id);
        } else {
            LOGGER.debug("failed: "
                    + name
                    + (r.hasMode() ? ":" + r.getMode() : "")
                    + "@" + id);
        }
        if (r.hasWarning()) {
        	for (Warn w : r.getWarnings()) {
                warn.warn(name + ":" + w.getMessage());
        	}
        }
	}
    public abstract void runSessions();

    @Override
    public boolean isRunning() {
        return running;
    }
    @Override
    public boolean isSuccessful() {
    	return success;
    }

    protected Result doAction(SessionContainer<T> sc) {
    	T c = con.getObject();
    	target.beginUse(c);
    	try {
            return sc.doAction(sessionProf.getObject(), c);
    	} finally {
    		target.endUse(c);
    	}
    }

    protected SessionContainer<T> newSession() {
        State state = states.next();
        if (state != null) {
        	sessionProf.getObject().newSession();
            return new SessionContainer<T>(sfactory.create(), state);
        } else {
            return null;
        }
    }

    protected void waitTime(long msec) {
    	if (msec > 0 && running) {
    		try {
    			synchronized (this) {
    				this.wait(msec);
    			}
    		} catch (InterruptedException e) {
    			Thread.currentThread().interrupt();
    		}
    	}
    }

}
