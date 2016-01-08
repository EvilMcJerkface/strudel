package com.nec.strudel.workload.session.runner;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.instrument.stat.BinaryAccumulator;
import com.nec.strudel.instrument.stat.LongAccumulator;

@ThreadSafe
public class SessionStatAccumulator {
	private final LongAccumulator interaction = new LongAccumulator();
	private final AtomicInteger newSession = new AtomicInteger();
	private final BinaryAccumulator interactionSuccess =
			new BinaryAccumulator();
	public SessionStatAccumulator() {
	}

	public void interaction(long microSec) {
		interaction.event(microSec);
	}
	public void interactionResult(boolean success) {
		interactionSuccess.event(success);
	}
	public long getInteractionCount() {
		return interaction.count();
	}
	public long getInteractionTime() {
		return interaction.sum();
	}
	public void newSession() {
		newSession.incrementAndGet();
	}
	public int getNewSessionCount() {
		return newSession.get();
	}
	public long getSuccessCount() {
		return interactionSuccess.getTrueCount();
	}
	public long getFailureCount() {
		return interactionSuccess.getFalseCount();
	}
}
