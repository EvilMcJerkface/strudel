/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nec.strudel.instrument.stat;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.instrument.OperationStat;

@ThreadSafe
public class OperationMonitor extends SlidingWindow<LongAccumulator>
implements OperationStat {
    private static final long MICS_PER_MS = 1000;
    private static final long MS_PER_SECOND = 1000;

	public static OperationMonitor create(int windowSize, long stepMs) {
		return new OperationMonitor(windowSize, stepMs);
	}

	public OperationMonitor(int windowSize, long stepMs) {
		super(windowSize, stepMs);
	}

	@Override
	protected LongAccumulator newInstance() {
		return new LongAccumulator();
	}

	@Override
	public void operation(long microSec) {
		getCurrent().event(microSec);
	}


	@Override
	public double getOperationsPerSec() {
		Window<LongAccumulator>[] buffs = getPast();
		if (buffs.length == 0) {
			return Double.NaN;
		}
		long start = buffs[0].startTime();
		long finish = buffs[buffs.length - 1].finishTime();
		double sec = ((double) (finish - start))
				/ MS_PER_SECOND;
		if (sec == 0) {
			return Double.NaN;
		}
		long count = 0;
		for (Window<LongAccumulator> w : buffs) {
			LongAccumulator b = w.get();
			count += b.count();
		}
		return count / sec;
	}

	@Override
	public double getAverageOperationTime() {
		Window<LongAccumulator>[] buffs = getPast();
		long count = 0;
		long time = 0;
		for (Window<LongAccumulator> w : buffs) {
			LongAccumulator b = w.get();
			count += b.count();
			time += b.sum();
		}
		if (count == 0) {
			return Double.NaN;
		} else {
			double micro =
					((double) time) / count;
			return micro / MICS_PER_MS;
		}
	}


}
