package com.nec.strudel.workload.session.runner;

import java.util.List;

import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.workload.session.SessionProfiler;
import com.nec.strudel.workload.session.SessionProfilerImpl;
import com.nec.strudel.workload.session.SessionRunnerStat;


public final class SessionProfilerServerImpl implements SessionProfilerServer {
	private final ProfilerService profs;
	private final SessionStatMonitor mon;

	public SessionProfilerServerImpl(ProfilerService profs) {
		this.profs = profs;
		this.mon = new SessionStatMonitor();
	}

	@Override
	public Instrumented<? extends SessionProfiler> profiler() {
		Instrumented<SessionProfilerImpl> instr = profs.createProfiler(
				SessionProfilerImpl.class);
		instr.getObject().setMon(mon);
		return instr;
	}

	public void registerStat(List<RunnerStat> runners) {
		profs.register(new SessionRunnerStat(runners, mon));
	}


}
