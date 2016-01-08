package com.nec.strudel.workload.exec.populate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.ValidateReporter;
import com.nec.strudel.workload.exec.Report;
import com.nec.strudel.workload.exec.batch.WorkThread;
import com.nec.strudel.workload.populator.PopulateProfiler;

public class PopulateWorkThread<T, P> implements WorkThread {
	private static final Logger LOGGER =
		    Logger.getLogger(PopulateWorkThread.class);
	public static final int DELAYED_VALIDATION_SIZE = 2000;
	private final int id;
	private final PopulatePool<T, P> pool;
	private final Random rand;
	private boolean validate = false;
	private boolean delayedcheck = true;
	private final T con;
	private final List<P> paramBuffer;
	private LoggingValidateReporter reporter;
	private int buffSize;
	private volatile boolean running = false;
	private volatile boolean done = false;
	private volatile boolean success = false;
	private final PopulateProfiler prof;
	private final Target<T> target;
	public PopulateWorkThread(int id, PopulatePool<T, P> pool,
			Instrumented<T> con, 
			Target<T> target, PopulateProfiler prof,
			Random rand, boolean validate) {
		this(id, pool, con.getObject(), target,
				prof, rand, validate, LOGGER);
	}

	public PopulateWorkThread(int id, PopulatePool<T, P> pool,
			T con, Target<T> target, PopulateProfiler prof,
			Random rand, boolean validate,
			final Logger logger) {
		this.id = id;
		this.pool = pool;
		this.rand = rand;
		this.con = con;
		this.target = target;
		this.prof = prof;
		this.reporter = new LoggingValidateReporter(logger);
		this.buffSize = DELAYED_VALIDATION_SIZE;
		this.validate = validate;
		if (validate) {
			paramBuffer = new ArrayList<P>(
					buffSize);
		} else {
			paramBuffer = new ArrayList<P>(
					0);
		}
	}

	@Override
	public void run() {
		running = true;
		try {
			Populator<T, P> pop = pool.getPopulator();
			for (PopulateParam param = pool.next(rand);
					param != null; param = pool.next(rand)) {
				P p = pop.createParameter(param);
				target.beginUse(con);
				prof.start();
				pop.process(con, p);
				prof.done();
				target.endUse(con);
				if (validate) {
					pop.validate(con, p, reporter);
					if (delayedcheck) {
						flushValidateBufferIfFull();
						paramBuffer.add(p);
					}
				}
				if (!running) {
					success = false;
					return;
				}
			}
			if (validate && delayedcheck) {
				flushValidateBuffer();
			}
			success = true;
		} finally {
			running = false;
			done = true;
		}
	}
	/**
	 * TODO support double-check mode by calling this:
	 */
	public void setDelayedCheck(boolean doublecheck) {
		this.delayedcheck = doublecheck;
	}
	protected void flushValidateBufferIfFull() {
		if (paramBuffer.size() >= buffSize) {
			flushValidateBuffer();
		}
	}
	protected void flushValidateBuffer() {
			Populator<T, P> pop = pool.getPopulator();
			reporter.setDelayedCheck(true);
			for (P p : paramBuffer) {
				pop.validate(con, p, reporter);
			}
			reporter.setDelayedCheck(false);
			paramBuffer.clear();
	}

	@Override
	public Report getReport() {
		/**
		 * TODO report warns
		 */
		return Report.none();
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void stop() {
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}
	@Override
	public boolean isDone() {
		return done;
	}
	@Override
	public boolean isSuccessful() {
		return success;
	}
	static class LoggingValidateReporter implements ValidateReporter {
		private final Logger logger;
		private boolean delayedCheck;
		public LoggingValidateReporter(Logger logger) {
			this.logger = logger;
		}
		public void setDelayedCheck(boolean doubleCheck) {
			this.delayedCheck = doubleCheck;
		}

		@Override
		public void error(String message) {
			if (delayedCheck) {
				logger.warn(
					"delayed double check: " + message);
			} else {
				logger.warn(message);
			}
		}
	}
}
