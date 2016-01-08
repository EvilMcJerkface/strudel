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

import java.util.Arrays;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class SlidingWindow<A> {
    private static final long MS_PER_SECOND = 1000;

	private final int window;
	private final long intervalMs;
	private volatile long lastTick;
	private Window<A> current;
	@SuppressWarnings("unchecked")
	private Window<A>[] past = new Window[0];
	public SlidingWindow(int size, long intervalMs) {
		this.window = size;
		this.intervalMs = intervalMs;
	}
	protected synchronized void initialize(long time) {
		if (current == null) {
			current = new Window<A>(newInstance(),
					time);
		}
	}

	protected abstract A newInstance();

	protected A getCurrent() {
		long time = System.currentTimeMillis();
		if (current == null) {
			initialize(time);
		}
		if (time > lastTick + intervalMs) {
			tick(time);
		}
		return current.get();
	}
	protected Window<A>[] getPast() {
		if (current != null) {
			long time = System.currentTimeMillis();
			if (time > lastTick + intervalMs) {
				tick(time);
			}
		}
		return past;
	}

	private synchronized void tick(long time) {
		if (time > lastTick + intervalMs) {
			lastTick = time;
			Window<A> old = current;
			current = new Window<A>(newInstance(), time);
			old.finish(time);
			if (past.length < window) {
				Window<A>[] buffs =
					Arrays.copyOf(past, past.length + 1);
				buffs[buffs.length - 1] = old;
				past = buffs;
			} else {
				@SuppressWarnings("unchecked")
				Window<A>[] buffs = new Window[window];
				for (int i = 0; i < buffs.length - 1; i++) {
					buffs[i] = past[i + 1];
				}
				buffs[buffs.length - 1] = old;
				past = buffs;
			}
		}
	}
	public double averageValue(ValueCounter<A> c) {
		Window<A>[] buffs = getPast();
		long count = 0;
		long time = 0;
		for (Window<A> w : buffs) {
			A b = w.get();
			count += c.count(b);
			time += c.value(b);
		}
		if (count == 0) {
			return Double.NaN;
		} else {
			return ((double) time) / count;
		}
	}
	public double countPerSec(Counter<A> c) {
		Window<A>[] buffs = getPast();
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
		for (Window<A> w : buffs) {
			A b = w.get();
			count += c.count(b);
		}
		return count / sec;

	}
	public interface ValueCounter<A> {
		long count(A buff);
		long value(A buff);
	}
	public interface Counter<A> {
		long count(A buff);
	}
	public static class Window<A> {
		private final long startTime;
		private long finishTime = -1;
		private final A value;
		Window(A value, long time) {
			this.value = value;
			this.startTime = time;
		}
		public A get() {
			return value;
		}
		public long startTime() {
			return startTime;
		}
		public synchronized void finish(long time) {
			finishTime = time;
		}
		public synchronized long finishTime() {
			return finishTime;
		}

	}

}
