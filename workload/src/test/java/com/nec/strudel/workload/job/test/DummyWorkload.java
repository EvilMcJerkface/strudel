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
package com.nec.strudel.workload.job.test;

import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.metrics.Report;
import com.nec.strudel.workload.exec.WorkExec;
import com.nec.strudel.workload.exec.Workload;
import com.nec.strudel.workload.exec.batch.BatchExec;
import com.nec.strudel.workload.exec.batch.WorkThread;
import com.nec.strudel.workload.job.ConfigParam;
import com.nec.strudel.workload.job.WorkConfig;
import com.nec.strudel.metrics.Output;
import com.nec.strudel.workload.state.WorkState;

public class DummyWorkload implements Workload {
	public DummyWorkload() {
	}

	@Override
	public WorkExec createWorkExec(WorkConfig work,
			WorkState state,
			ProfilerService profs) {
		int size = work.numOfThreads();
		WorkThread[] works = new WorkThread[size];
		for (int i = 0; i < size; i++) {
			works[i] = new DummyWorkThread(i);
		}
		return BatchExec.create(state, works);
	}

	@Override
	public Output output(ConfigParam param) {
		return Output.empty();
	}

	static class DummyWorkThread implements WorkThread {
		private final int id;
		private volatile boolean done = false;
		public DummyWorkThread(int id) {
			this.id = id;
		}
		@Override
		public void run() {
			done = true;
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public Report getReport() {
			return Report.none();
		}

		@Override
		public void stop() {
			
		}

		@Override
		public boolean isRunning() {
			return false;
		}
		@Override
		public boolean isDone() {
			return done;
		}
		@Override
		public boolean isSuccessful() {
			return done;
		}
	}
}
