package com.nec.strudel.workload.session.runner.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.exec.Report;
import com.nec.strudel.workload.session.SessionContainer;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.session.SessionProfiler;
import com.nec.strudel.workload.session.StateFactory;
import com.nec.strudel.workload.session.runner.SessionProfilerServer;
import com.nec.strudel.workload.util.TimeValue;
import com.nec.strudel.workload.util.event.EventSeq;
import com.nec.strudel.workload.util.event.TimedEvent;

public class SessionEventSeq<T> implements EventSeq<ActionResult<T>> {
    private final SessionFactory<T> sfactory;
    private final StateFactory states;
    private final SessionProfilerServer sps;
    private int sessionSize;
    private final Target<T> target;
    private final ProfilerService profs;
    private final List<SessionContext<T>> contexts =
    		new ArrayList<SessionContext<T>>();
    private long startSlackMS;
    private final Random rand;

	public SessionEventSeq(SessionFactory<T> sfactory,
			StateFactory states, int sessionSize,
			Target<T> target, SessionProfilerServer sps,
			ProfilerService profs, Random rand) {
		this.sfactory = sfactory;
		this.states = states;
		this.target = target;
		this.profs = profs;
		this.sessionSize = sessionSize;
		this.sps = sps;
		this.rand = new Random(rand.nextLong());
	}

	@Override
	public Collection<TimedEvent<ActionResult<T>>> next(ActionResult<T> res) {
		Result r = res.getResult();
		inspect(r);

		SessionEvent<T> next = res.nextAction();
		if (next != null) {
			return Arrays.asList((TimedEvent<ActionResult<T>>) next);
		}
		SessionContainer<T> sc = newSession();
		if (sc != null) {
			SessionEvent<T> newSession = res.newSession(sc);
			return Arrays.asList((TimedEvent<ActionResult<T>>) newSession);
		}
		return Collections.emptyList();
	}
	void inspect(Result r) {
		/**
		 * TODO do something
		 */
	}
	public void setStartSlack(TimeValue slackTime) {
		startSlackMS = slackTime.toMillis();
	}
	private long slackDelay() {
		if (startSlackMS > 0) {
			return rand.nextInt((int) startSlackMS);
		} else {
			return 0;
		}
	}

	@Override
	public Collection<TimedEvent<ActionResult<T>>> start() {
		List<TimedEvent<ActionResult<T>>> list =
				new ArrayList<TimedEvent<ActionResult<T>>>(sessionSize);
		for (int i = 0; i < sessionSize; i++) {
			SessionContainer<T> sc = newSession();
			if (sc == null) {
				break;
			}
			long delay = slackDelay();
			if (delay > 0) {
				sc.delay(delay);
			}
			list.add(newSessionEvent(sc));
		}
		return list;
	}

	SessionEvent<T> newSessionEvent(SessionContainer<T> sc) {
		return new SessionEvent<T>(sc, createCtxt());
	}

	public Report getReport() {
		Report[] reports;
		synchronized (contexts) {
			reports = new Report[this.contexts.size()];
			for (int i = 0; i < reports.length; i++) {
				reports[i] = contexts.get(i).getReport();
			}
		}
		return Report.aggregate(reports);
	}

	private SessionContext<T> createCtxt() {
		Instrumented<T> con = target.open(profs);
		Instrumented<? extends SessionProfiler> p = sps.profiler();
		SessionProfiler prof = p.getObject();
		prof.newSession();
		SessionContext<T> ctxt =
			new SessionContext<T>(target, con, p);
		synchronized (contexts) {
			contexts.add(ctxt);
		}
		return ctxt;
	}

	@Override
	public Collection<TimedEvent<ActionResult<T>>> poll() {
		/**
		 * TODO give a set of new sessions when
		 * the session concurrency increases.
		 */
		return Collections.emptyList();
	}
    protected SessionContainer<T> newSession() {
        State state = states.next();
        if (state != null) {
            return new SessionContainer<T>(sfactory.create(), state);
        } else {
            return null;
        }
    }

}
