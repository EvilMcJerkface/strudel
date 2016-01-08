package com.nec.strudel.instrument.impl;

import java.util.concurrent.TimeUnit;

public class MeasurementTimer implements MeasurementState {
	private volatile long completeTime = Long.MAX_VALUE;
	private volatile boolean started = false;
	private volatile boolean completed = false;

	public void start(int seconds) {
		this.started = true;
		this.completeTime = System.currentTimeMillis()
				+ TimeUnit.SECONDS.toMillis(seconds);
	}
	public boolean isDone() {
		return System.currentTimeMillis()
				>= completeTime;
	}

	@Override
	public boolean isMeasuring() {
		if (!started) {
			return false;
		}
		if (completed) {
			return false;
		} else if (isDone()) {
			completed = true;
			return false;
		}
		return true;
	}

}