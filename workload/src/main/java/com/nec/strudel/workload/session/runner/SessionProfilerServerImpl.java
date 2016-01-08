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
