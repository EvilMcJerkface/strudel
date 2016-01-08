package com.nec.strudel.instrument.stat;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class BinaryAccumulator {
	private final AtomicLong trueCount = new AtomicLong(0);
	private final AtomicLong falseCount = new AtomicLong(0);

	public void event(boolean mode) {
		if (mode) {
			trueCount.incrementAndGet();
		} else {
			falseCount.incrementAndGet();
		}
	}
	public long getTrueCount() {
		return trueCount.get();
	}
	public long getFalseCount() {
		return falseCount.get();
	}
}