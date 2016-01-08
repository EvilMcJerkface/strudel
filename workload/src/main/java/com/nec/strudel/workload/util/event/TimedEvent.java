package com.nec.strudel.workload.util.event;

import java.util.concurrent.Callable;

public interface TimedEvent<R> extends Callable<R> {
	/**
	 * The time this event should be executed.
	 * @return a long value that corresponds to System.currentTimeMillis().
	 */
	long getTime();

}