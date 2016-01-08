package com.nec.strudel.workload.exec;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.json.JsonObject;

import com.nec.strudel.Closeable;
import com.nec.strudel.workload.util.TimeValue;

public abstract class WorkExec implements Closeable {
	private final int numOfThreads;
	private Closeable[] closeables;

	public WorkExec(int numOfThreads,
			Closeable... closeables) {
		this.numOfThreads = numOfThreads;
		this.closeables = closeables;
	}


	public int numOfThreads() {
		return numOfThreads;
	}

	@Override
	public synchronized void close() {
		for (Closeable c : closeables) {
			c.close();
		}
	}
	public synchronized void addCloseable(Closeable... cs) {
		Closeable[] newcs = Arrays.copyOf(closeables, closeables.length + cs.length);
		for (int i = 0; i < cs.length; i++) {
			newcs[i + closeables.length] = cs[i];
		}
		this.closeables = newcs;
	}

	public abstract String getState();

	public abstract Report getReport();

	public abstract void start(TimeValue slack);

	public abstract void operate(String name, JsonObject data);

	public abstract void stop();

	public abstract boolean terminate(long timeout, TimeUnit unit)
			throws InterruptedException;

}
