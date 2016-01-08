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
package com.nec.strudel.workload.util.event;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * Executor to run timed events.
 *
 * @param <R>
 */
public class EventExecutor<R> {
	private static final Logger LOGGER =
			Logger.getLogger(EventExecutor.class);
	private static final int NUM_MANAGER_THREADS = 2;
	private final BlockingQueue<R> resultQueue =
			new LinkedBlockingQueue<R>();
	private final EventSeq<R> seq;
	private final EventScheduler<R> scheduler;
	private final EventCreator<R> creator;
	private final ExecutorService exec;

	/**
	 * Creates event scheduler.
	 * @param seq the sequence of timed events
	 * @param mpLevel
	 */
	public EventExecutor(EventSeq<R> seq, int mpLevel) {
		this.seq = seq;
		this.exec = Executors.newFixedThreadPool(mpLevel + NUM_MANAGER_THREADS);
		this.scheduler = new EventScheduler<R>(exec, resultQueue);
		this.creator = new EventCreator<R>(scheduler, seq, resultQueue);
		
	}
	/**
	 * Starts event execution.
	 */
	public void start() {
		scheduler.addAll(seq.start());
		exec.execute(scheduler);
		exec.execute(creator);
	}
	/**
	 * Stops event execution. It does not terminate
	 * events that are being executed. Use awaitTermination()
	 * to make sure everything is done.
	 */
	public void stop() {
		scheduler.stop();
		creator.stop();
	}
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		exec.shutdown();
		return exec.awaitTermination(timeout, unit);
	}
	public boolean hasFailure() {
		return scheduler.hasFailure();
	}

	private static class EventScheduler<R> implements Runnable {
		private static final int INIT_CAPACITY = 1000;
		private static final long SLEEP_FOR_EMPTY = 100;
		private volatile boolean running = true;
		private volatile boolean sleeping = false;
		private volatile boolean hasFailure = false;
		private final Executor exec;
		private final PriorityBlockingQueue<TimedEvent<R>> queue =
				new PriorityBlockingQueue<TimedEvent<R>>(INIT_CAPACITY, EVENT_CMP);
		private final BlockingQueue<R> resultQueue;
		public EventScheduler(Executor exec,
				BlockingQueue<R> resultQueue) {
			this.exec = exec;
			this.resultQueue = resultQueue;
		}
		@Override
		public void run() {
			try {
				schedule();
			} catch (InterruptedException e) {
				running = false;
				Thread.currentThread().interrupt();
				LOGGER.error("event scheduler interrupted", e);
			}
		}
		void schedule() throws InterruptedException {
			while (running) {
				TimedEvent<R> e = queue.peek();
				if (e != null) {
					long time = System.currentTimeMillis();
					if (time >= e.getTime()) {
						execute(queue.take());
					} else {
						sleep(e.getTime() - time);
					}
				} else {
					sleep(SLEEP_FOR_EMPTY);
				}
			}
		}
		private void execute(final TimedEvent<R> event) {
			exec.execute(new Runnable() {		
				@Override
				public void run() {
					try {
						R res = event.call();
						resultQueue.put(res);
					} catch (Exception e) {
						hasFailure = true;
						LOGGER.error("event execution failed", e);
					}
					
				}
			});
		}
		private void sleep(long sleepMs)
				throws InterruptedException {
			synchronized (this) {
				sleeping = true;
				this.wait(sleepMs);
				sleeping = false;
			}	
		}
		public boolean hasFailure() {
			return hasFailure;
		}
		public void addAll(Collection<TimedEvent<R>> events) {
			queue.addAll(events);
		}
		public void put(TimedEvent<R> event) {
			queue.put(event);
		}
		public void wakeup() {
			synchronized (this) {
				if (sleeping) {
					this.notify();
				}
			}
		}
		public void stop() {
			running = false;
			wakeup();
		}
	}

	/**
	 * Receives the results of finished events and
	 * generates next events based on the given event
	 * sequence. Generated events are given to the
	 * scheduler.
	 * @param <R>
	 */
	private static class EventCreator<R> implements Runnable {
		private volatile boolean running = true;
		private static final long TIMEOUT = 100;
		private final EventScheduler<R> scheduler;
		private final EventSeq<R> seq;
		private final BlockingQueue<R> resultQueue;
		public EventCreator(EventScheduler<R> scheduler,
				EventSeq<R> seq, BlockingQueue<R> resultQueue) {
			this.scheduler = scheduler;
			this.seq = seq;
			this.resultQueue = resultQueue;
		}
		@Override
		public void run() {
			try {
				execute();
			} catch (InterruptedException e) {
				running = false;
				Thread.currentThread().interrupt();
				LOGGER.error("event creator interrupted", e);
			}
		}
		void execute() throws InterruptedException {
			while (running) {
				R res = resultQueue.poll(
						TIMEOUT, TimeUnit.MILLISECONDS);
				boolean newevents = false;
				if (res != null) {
					for (TimedEvent<R> next : seq.next(res)) {
						scheduler.put(next);
						newevents = true;
					}
				}
				for (TimedEvent<R> next : seq.poll()) {
					scheduler.put(next);
					newevents = true;
				}
				if (newevents) {
					flushAndNotify();
				}
			}
			
		}
		private void flushAndNotify() {
			R res;
			while ((res = resultQueue.poll()) != null) {
				for (TimedEvent<R> next : seq.next(res)) {
					scheduler.put(next);
				}
			}
			scheduler.wakeup();
		}
		public void stop() {
			running = false;
		}
	}

	@SuppressWarnings("rawtypes")
	static final Comparator<TimedEvent> EVENT_CMP =
			new Comparator<TimedEvent>() {
				@Override
				public int compare(TimedEvent o1, TimedEvent o2) {
					return Long.compare(o1.getTime(), o2.getTime());
				}
			};
}
