package com.nec.strudel.workload.job;

import java.util.Random;

public final class TaskUtil {

	private TaskUtil() {
	}

	public static long toSeed(String seed) {
		if (seed.matches("^\\d+$")) {
			return Long.parseLong(seed);
		} else {
			return seed.hashCode();
		}
	}
	/**
	 * If randomSeed is a non-empty string, generates a
	 * random with a seed. Otherwise, generates a random
	 * without a seed. If the randomSeed is a sequence of
	 * digits, it is parsed as a long value, which is used
	 * as a seed. Otherwise, the hash code of the string
	 * is used as a seed.
	 * @param randomSeed
	 * @return an instance of Random
	 */
	public static Random getRandom(String randomSeed) {
		if (randomSeed != null && !randomSeed.isEmpty()) {
			return new Random(TaskUtil.toSeed(randomSeed));
		} else {
			return new Random();
		}
	}
}
