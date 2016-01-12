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
