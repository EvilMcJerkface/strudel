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
package com.nec.strudel.workload.session;

import java.util.List;

import com.nec.strudel.management.resource.Getter;
import com.nec.strudel.management.resource.ManagedResource;
import com.nec.strudel.workload.session.runner.RunnerStat;
import com.nec.strudel.workload.session.runner.SessionStatMonitor;

@ManagedResource(description = "monitors the state of session runners")
public class SessionRunnerStat {
	private final RunnerStat stat;
	private final int numOfThreads;
	private final SessionStatMonitor mon;
	public SessionRunnerStat(List<RunnerStat> runners,
			SessionStatMonitor mon) {
		this.stat = new MultiThreadRunnerStat(runners);
		this.numOfThreads = runners.size();
		this.mon = mon;
	}
	public SessionRunnerStat(int sessionConcurrency,
			int numOfThreads,
			SessionStatMonitor mon) {
		this.stat = new FixedRunnerStat(
				sessionConcurrency);
		this.numOfThreads = numOfThreads;
		this.mon = mon;
	}
	@Getter(description = "number of threads per worker")
	public int getNumOfThreads() {
		return numOfThreads;
	}

	@Getter(description = "session concurrency per worker")
	public double getAvgSessionConcurrency() {
		return stat.getSessionConcurrency();
	}

	@Getter(description = "ratio that an interaction finishes successfully")
	public double getSuccessRatio() {
		return mon.getSuccessRatio();
	}

	@Getter(description = "average execution time (ms) for an interaction")
	public double getAvgInteractionTime() {
		return mon.getAverageInteractionTime();
	}

	@Getter(description = "number of executed interactions per second")
	public double getInteractionsPerSec() {
		return mon.getInteractionsPerSec();
	}

	@Getter(description = "number of new sessions started per second")
	public double getNewSessionsPerSec() {
		return mon.getNewSessionsPerSec();
	}
	static class FixedRunnerStat implements RunnerStat {
		private final int sessionConcurrency;
		public FixedRunnerStat(int sessionConcurrency) {
			this.sessionConcurrency = sessionConcurrency;
		}
		@Override
		public int getSessionConcurrency() {
			return sessionConcurrency;
		}
		
	}
	static class MultiThreadRunnerStat implements RunnerStat {
		private final List<RunnerStat> runners;
		public MultiThreadRunnerStat(List<RunnerStat> runners) {
			this.runners = runners;
		}
		@Override
		public int getSessionConcurrency() {
			int len = 0;
			for (RunnerStat r : runners) {
				len += r.getSessionConcurrency();
			}
			return len;
		}
		
	}
}
