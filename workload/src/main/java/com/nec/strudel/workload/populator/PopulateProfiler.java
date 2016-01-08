package com.nec.strudel.workload.populator;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PopulateProfiler {
	private final PopulateStat stat;
	private long startNano;

	public PopulateProfiler(PopulateStat stat) {
		this.stat = stat;
	}
	public void start() {
		startNano = System.nanoTime();
	}
	public void done() {
		long timeMicro = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startNano);
		stat.taskDone(timeMicro);
	}
}
