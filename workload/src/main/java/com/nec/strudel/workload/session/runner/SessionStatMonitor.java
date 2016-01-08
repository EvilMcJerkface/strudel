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
package com.nec.strudel.workload.session.runner;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.instrument.stat.SlidingWindow;

@ThreadSafe
public class SessionStatMonitor extends SlidingWindow<SessionStatAccumulator> {
	public static final int DEFAULT_SIZE = 5;
	public static final long DEFAULT_INTERVAL = 1000;
    private static final long MICS_PER_MS = 1000;

	public SessionStatMonitor() {
		super(DEFAULT_SIZE, DEFAULT_INTERVAL);
	}
	@Override
	protected SessionStatAccumulator newInstance() {
		return new SessionStatAccumulator();
	}

	public void interaction(long microSec, boolean success) {
		SessionStatAccumulator s = getCurrent();
		s.interaction(microSec);
		s.interactionResult(success);
	}
	public void newSession() {
		getCurrent().newSession();
	}


	public double getNewSessionsPerSec() {
		return countPerSec(new Counter<SessionStatAccumulator>() {
			@Override
			public long count(SessionStatAccumulator buff) {
				return buff.getNewSessionCount();
			}
		});
	}

	public double getInteractionsPerSec() {
		return countPerSec(new Counter<SessionStatAccumulator>() {
			@Override
			public long count(SessionStatAccumulator buff) {
				return buff.getInteractionCount();
			}
		});
	}

	public double getAverageInteractionTime() {
		double micro = averageValue(
		new ValueCounter<SessionStatAccumulator>() {
			@Override
			public long count(SessionStatAccumulator buff) {
				return buff.getInteractionCount();
			}

			@Override
			public long value(SessionStatAccumulator buff) {
				return buff.getInteractionTime();
			}
		});
		if (micro == Double.NaN) {
			return Double.NaN;
		}
		return micro / MICS_PER_MS;
	}

	public double getSuccessRatio() {
		Window<SessionStatAccumulator>[] buffs = getPast();
		long trueCount = 0;
		long falseCount = 0;
		for (Window<SessionStatAccumulator> w : buffs) {
			SessionStatAccumulator b = w.get();
			trueCount += b.getSuccessCount();
			falseCount += b.getFailureCount();
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
