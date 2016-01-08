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
