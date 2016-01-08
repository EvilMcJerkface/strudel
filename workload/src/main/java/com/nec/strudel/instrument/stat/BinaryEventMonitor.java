package com.nec.strudel.instrument.stat;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.instrument.BinaryEventStat;

@ThreadSafe
public class BinaryEventMonitor extends SlidingWindow<BinaryAccumulator> implements BinaryEventStat {

	public BinaryEventMonitor(int windowSize, long stepMs) {
		super(windowSize, stepMs);
	}

	@Override
	protected BinaryAccumulator newInstance() {
		return new BinaryAccumulator();
	}
	@Override
	public void event(boolean mode) {
		getCurrent().event(mode);
	}
	@Override
	public double getTrueRatio() {
		Window<BinaryAccumulator>[] buffs = getPast();
		long trueCount = 0;
		long falseCount = 0;
		for (Window<BinaryAccumulator> w : buffs) {
			BinaryAccumulator b = w.get();
			trueCount += b.getTrueCount();
			falseCount += b.getFalseCount();
		}
		if (falseCount == 0) {
			if (trueCount == 0) {
				return Double.NaN; // UNDEF
			}
			return 1;
		} else if (trueCount == 0) {
			return 0;
		} else {
			return ((double) trueCount) / (trueCount + falseCount);
		}
	}
}
