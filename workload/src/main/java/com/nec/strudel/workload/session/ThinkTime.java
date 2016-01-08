package com.nec.strudel.workload.session;

import javax.annotation.Nullable;

import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.workload.session.WaitTime.ConstantWait;
import com.nec.strudel.workload.session.WaitTime.WaitTimer;
import com.nec.strudel.workload.util.TimeValue;

/**
 * A class that generates think time.
 * <p>
 * The following example specifies 1.5 sec constant
 * time for preparation time (time before an interaction)
 * and average 3 sec exponential time (with max 10 sec limit)
 * for think time (time after an interaction).
 * <pre>
 * { "before": { "time": 1.5 },
 *   "after": { "avg": 3, "max": 10}
 * }
 * </pre>
 *
 */
public class ThinkTime {
	public static ThinkTime noTime() {
		return new ThinkTime();
	}
	private static final WaitTimer NO_WAIT = new ConstantWait(0);
	private TimeConfig before = null;
	private TimeConfig after = null;
	@Nullable
	public TimeConfig getBefore() {
		return before;
	}
	public void setBefore(TimeConfig before) {
		this.before = before;
	}
	@Nullable
	public TimeConfig getAfter() {
		return after;
	}
	public void setAfter(TimeConfig after) {
		this.after = after;
	}
	public WaitTimer getBeforeTimer() {
		if (before == null) {
			return NO_WAIT;
		}
		return before.getTimer();
	}
	public WaitTimer getAfterTimer() {
		if (after == null) {
			return NO_WAIT;
		}
		return after.getTimer();
	}
	public boolean hasBefore() {
		return before != null;
	}
	public boolean hasAfter() {
		return after != null;
	}

	public static class TimeConfig {
		private double time = 0;
		private double avg = 0;
		private double max = 0;
		public double getTime() {
			return time;
		}
		public void setTime(double time) {
			this.time = time;
		}
		public double getAvg() {
			return avg;
		}
		public void setAvg(double avg) {
			this.avg = avg;
		}
		public double getMax() {
			return max;
		}
		public void setMax(double max) {
			this.max = max;
		}
		public WaitTimer getTimer() {
			if (time > 0) {
				return WaitTime.createConstant(
						TimeValue.seconds(time));
			}
			if (avg > 0 && max > avg) {
				return WaitTime.createExponential(
						TimeValue.seconds(avg),
						TimeValue.seconds(max));
			} else if (avg == 0 && max == 0) {
			    return NO_WAIT; // no wait
			} else {
				throw new ConfigException(
				"invalid wait time specification");
			}
		}
	}
}