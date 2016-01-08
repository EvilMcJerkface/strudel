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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.param.ParamConfig;
import com.nec.strudel.param.ParamSequence;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.exec.WorkExec;
import com.nec.strudel.workload.exec.batch.BatchExec;
import com.nec.strudel.workload.exec.batch.WorkThread;
import com.nec.strudel.workload.job.ThreadIds;
import com.nec.strudel.workload.job.WorkNodeInfo;
import com.nec.strudel.workload.out.Output;
import com.nec.strudel.workload.session.SessionConfig;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.session.SessionProfilerImpl;
import com.nec.strudel.workload.session.StateFactory;
import com.nec.strudel.workload.state.WorkState;

public class SimpleClosedSessionExecFactory<T> implements
		SessionExecFactory<T> {
	public static final String TYPE = "simple_closed";


	@Override
	public void initialize(SessionConfig<T> conf) {
	}

	@Override
	public WorkExec create(WorkNodeInfo node,
			Target<T> target,
			SessionFactory<T> sfactory, WorkState state, ProfilerService profs,
			ParamConfig pconf, Random rand) {
		ThreadIds ids = new ThreadIds(node.getNodeId());
		SessionProfilerServerImpl rep = new SessionProfilerServerImpl(profs);
		List<RunnerStat> runners =
				new ArrayList<RunnerStat>();
		ParamSequence[] seqs = pconf
				.createParamSeqVector(node.getNodeId(), node.getNodeNum(), node.numOfThreads());
		WorkThread[] ws = new WorkThread[seqs.length];
		for (int i = 0; i < ws.length; i++) {
			StateFactory stateFactory =
					new StateFactory(seqs[i],
							new Random(rand.nextLong()));
			SimpleClosedSessionRunner<T> runner =
					new SimpleClosedSessionRunner<T>(ids.idOf(i),
							sfactory, target.open(profs),
							target, stateFactory,
							rep.profiler());
			runners.add(runner);
			ws[i] = runner;
		}
		rep.registerStat(runners);
		return BatchExec.create(state, ws, target);
	}

	@Override
	public Output output(SessionConfig<T> conf) {
		return SessionProfilerImpl.output();
	}

	@Override
	public String getType() {
		return TYPE;
	}
}
