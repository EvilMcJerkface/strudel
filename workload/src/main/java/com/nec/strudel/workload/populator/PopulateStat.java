package com.nec.strudel.workload.populator;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.instrument.OperationStat;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.management.resource.Getter;
import com.nec.strudel.management.resource.ManagedResource;
import com.nec.strudel.management.resource.ResourceName;

@ThreadSafe
@ManagedResource(description =
"monitors the performance of populate tasks")
public class PopulateStat {
	public static final int WINDOW_SIZE = 5;
	public static final long WINDOW_STEP_MS = 1000;
	private final OperationStat mon;
	private final String name;
	private final int threadNum;
	public PopulateStat(String name, int threadNum, ProfilerService profs) {
		mon = profs.createOperationStat(WINDOW_SIZE, WINDOW_STEP_MS);
		this.name = name;
		this.threadNum = threadNum;
	}
	@ResourceName
	public String getName() {
		return name;
	}

	@Getter
	public int getThreadNum() {
		return threadNum;
	}

	@Getter
	public double getTasksPerSec() {
		return mon.getOperationsPerSec();
	}
	@Getter
	public double getAvgTaskTime() {
		return mon.getAverageOperationTime();
	}
	public PopulateProfiler profiler() {
		return new PopulateProfiler(this);
	}
	public void taskDone(long microSec) {
		mon.operation(microSec);
	}
}
