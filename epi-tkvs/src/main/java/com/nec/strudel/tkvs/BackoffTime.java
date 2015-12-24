package com.nec.strudel.tkvs;

public interface BackoffTime {
	/**
	 * Returns the msec to wait
	 * @return -1 if it reaches
	 * the trial limit.
	 */
	long failed();
}