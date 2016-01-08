/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
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
package com.nec.strudel.tkvs;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.management.resource.Getter;
import com.nec.strudel.management.resource.ManagedResource;

@ThreadSafe
@ManagedResource(description =
"configuration of transaction retry backoff")
public class BackoffPolicy {
	public enum Type {
		EXPONENTIAL,
		LINEAR
	}
	public static final String PROP_TOTAL_MS = "tkvs.retry.max.total.ms";
	public static final String PROP_MAX_COUNT = "tkvs.retry.max.count";
	public static final String PROP_START_COUNT = "tkvs.retry.backoff.start.count";
	public static final String PROP_MAX_MS = "tkvs.retry.backoff.max.ms";
	public static final String PROP_INIT_MS = "tkvs.retry.backoff.init.ms";
	private static final int MAX_TRIAL = 100;
    private static final int TOTAL_WAIT = 10000; // 10 seconds
    private static final int MAX_WAIT = 1000; // 1 seconds
	private static final int START_BACKOFF = 4;

	private static final long INIT_WAIT = 1;
	private static final long WAIT_MSEC = 10;

	private Type type = Type.EXPONENTIAL;
	private final int maxTrial;
	private final int startBackoff;
	private final long initWait;
	private final long totalWait;
	private final long maxWait;
	private long waitStep = WAIT_MSEC;

	public BackoffPolicy() {
		this(new Properties());
	}
	public BackoffPolicy(Properties props) {
		this.totalWait = getLong(props, PROP_TOTAL_MS, TOTAL_WAIT);
		this.maxTrial = getInt(props, PROP_MAX_COUNT, MAX_TRIAL);
		this.startBackoff = getInt(props, PROP_START_COUNT,
				START_BACKOFF);
		this.maxWait = getLong(props, PROP_MAX_MS, MAX_WAIT);
		this.initWait = getLong(props, PROP_INIT_MS, INIT_WAIT);
		
	}
	public BackoffTime newBackoff() {
		if (type == Type.EXPONENTIAL) {
			return exponentialBackoff();
		} else {
			return linearBackoff();
		}
	}
	private BackoffTime exponentialBackoff() {
		return new ExponentialBackoff(this);
	}
	private BackoffTime linearBackoff() {
		return new LinearBackoff(this);
	}

	@Getter
	public String getType() {
		return type.name();
	}
	@Getter
	public int getMaxTrial() {
		return maxTrial;
	}
	@Getter
	public long getMaxTotalWaitMS() {
		return totalWait;
	}
	@Getter
	public long getInitWaitMS() {
		return initWait;
	}
	@Getter
	public long getMaxWaitMS() {
		return maxWait;
	}
	@Getter
	public long getWaitStepMS() {
		return waitStep;
	}
	@Getter
	public int getStartBackoff() {
		return startBackoff;
	}

	public <T> T call(Callable<T> callable, Class<?> exception)
			throws InterruptedException, RetryException {
		BackoffTime bot = this.newBackoff();
		while (true) {
			try {
				return callable.call();
			} catch (Exception e) {
				if (exception.isInstance(e)) {
					long wait = bot.failed();
					if (wait < 0) {
						throw new RetryException(
								"retry failed", true, e);
					} else if (wait > 0) {
						Thread.sleep(wait);
					}
				} else {
					throw new RetryException(
					"unexpected exception", false, e);
				}
			}
		}
	}

	static int getInt(Properties prop, String name, int defaultValue) {
		String value = prop.getProperty(name);
		if (value == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	static long getLong(Properties prop, String name, long defaultValue) {
		String value = prop.getProperty(name);
		if (value == null) {
			return defaultValue;
		}
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
    static class ExponentialBackoff implements BackoffTime {
    	private Random rand = new Random();
    	private int numOfTrial = 0;
    	private final int startBackoff;
    	private final long maxTotalWait;
    	private long totalWait = 0;
    	private final long initialWait;
    	private long waitFactor = 1;
    	private final int maxWait;
    	private final int maxTrial;
    	ExponentialBackoff(BackoffPolicy pol) {
    		this.initialWait = pol.getInitWaitMS();
    		this.maxWait = (int) pol.getMaxWaitMS();
    		this.maxTrial = pol.getMaxTrial();
    		this.maxTotalWait = pol.getMaxTotalWaitMS();
    		this.startBackoff = pol.getStartBackoff();
    	}
		@Override
		public long failed() {
			if (numOfTrial > maxTrial) {
				return -1;
			}
			numOfTrial++;
			if (numOfTrial <= startBackoff) {
				return 0;
			}
			long expWait = initialWait * waitFactor;
			int max = (int) expWait;
			if (expWait > maxWait) {
				max = maxWait;
			} else {
				waitFactor *= 2;
			}
			int w = rand.nextInt(max);
			totalWait += w;
			if (totalWait > maxTotalWait) {
				return -1;
			}
			return w;
		}
    }
    static class LinearBackoff implements BackoffTime {
    	private int numOfTrial = 0;
    	private final int startBackoff;
    	private final long maxWait;
    	private final int maxTrial;
    	private long waitTime;
    	private final long waitStep;
    	LinearBackoff(BackoffPolicy pol) {
    		this.maxWait = pol.getMaxWaitMS();
    		this.maxTrial = pol.getMaxTrial();
    		this.startBackoff = pol.getStartBackoff();
    		this.waitTime = pol.getInitWaitMS();
    		this.waitStep = pol.getWaitStepMS();
    	}
		@Override
		public long failed() {
			if (numOfTrial > maxTrial) {
				return -1;
			}
			numOfTrial++;
			if (numOfTrial <= startBackoff) {
				return 0;
			}
			long wait = waitTime;
			if (wait > maxWait) {
				wait = maxWait;
			} else {
				waitTime += waitStep;
			}
			return wait;
		}
    }
    public static Builder builder() {
    	return new Builder();
    }
    public static class Builder {
    	Properties props = new Properties();
    	public BackoffPolicy build() {
    		return new BackoffPolicy(props);
    	}
    	public Builder maxTotalMS(long msec) {
    		props.put(PROP_TOTAL_MS, msec);
    		return this;
    	}
    	public Builder maxTrial(int count) {
    		props.put(PROP_MAX_COUNT, count);
    		return this;
    	}
    	public Builder startBackoff(int count) {
    		props.put(PROP_START_COUNT, count);
    		return this;
    	}
    	public Builder initWaitMS(long msec) {
    		props.put(PROP_INIT_MS, msec);
    		return this;
    	}
    	public Builder maxWaitMS(long msec) {
    		props.put(PROP_MAX_MS, msec);
    		return this;
    	}
    }

}