package com.nec.strudel.workload.exec.batch;

import com.nec.strudel.workload.exec.Report;



/**
 * A class that emulates one thread of a workload.
 * @author tatemura
 *
 */
public interface WorkThread extends Runnable {
	/**
	 * checks if work is running.
	 * @return false if work
	 * has not been started, run()
	 * is done, or stop() request
	 * is accepted.
	 */
	boolean isRunning();

	/**
	 * checks if work is done
	 * @return true if run() has
	 * been executed and done.
	 */
	boolean isDone();

	boolean isSuccessful();

	int getId();

    void stop();

    Report getReport();
}
