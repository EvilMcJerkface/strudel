package com.nec.strudel.instrument.stat;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;


@ThreadSafe
public class LongAccumulator {
	private final AtomicLong count = new AtomicLong();
	private final AtomicLong sum = new AtomicLong();
	public LongAccumulator() {
	}
	public void event(long value) {
		count.incrementAndGet();
		sum.addAndGet(value);
	}
	public long count() {
		return count.get();
	}
	public long sum() {
		return sum.get();
	}

}