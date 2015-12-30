package com.nec.strudel.tkvs.store.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.instrument.GetOperationListener;
import com.nec.strudel.instrument.OperationStat;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.management.resource.Getter;
import com.nec.strudel.management.resource.ManagedResource;
import com.nec.strudel.management.resource.ResourceName;

@ThreadSafe
@ManagedResource(description =
"monitors the performance of transaction")
public class TransactionStat {
	public static final int WINDOW_SIZE = 5;
	public static final long WINDOW_STEP_MS = TimeUnit.SECONDS.toMillis(1);
	public static TransactionStat create(String name, ProfilerService profs) {
		return new TransactionStat(name, profs, WINDOW_SIZE, WINDOW_STEP_MS);
	}
	public static TransactionStat create(String name, ProfilerService profs,
			int windowSize, long windowStepMs) {
		return new TransactionStat(name, profs, windowSize, windowStepMs);
	}

	private final String name;
	private final OperationStat commitMon;
	private final OperationStat getMon;
	public TransactionStat(String name, ProfilerService profs, int size, long step) {
		this.name = name;
		commitMon = profs.createOperationStat(size, step);
		getMon = profs.createOperationStat(size, step);
	}

	@GetOperationListener("commit")
	public OperationStat commitMonitor() {
		return commitMon;
	}

	@GetOperationListener("get")
	public OperationStat getMonitor() {
		return getMon;
	}

	@ResourceName
	public String getName() {
		return name;
	}
	@Getter
	public double getAvgCommitTime() {
		return commitMon.getAverageOperationTime();
	}
	@Getter
	public double getCommitPerSec() {
		return commitMon.getOperationsPerSec();
	}
	@Getter
	public double getAvgGetTime() {
		return getMon.getAverageOperationTime();
	}
	@Getter
	public double getGetPerSec() {
		return getMon.getOperationsPerSec();
	}
}
