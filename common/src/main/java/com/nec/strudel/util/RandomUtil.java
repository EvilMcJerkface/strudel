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
package com.nec.strudel.util;

import java.util.List;
import java.util.Random;

public final class RandomUtil {

	private RandomUtil() {
	}

	public static <T> void permutate(Random rand, List<T> list) {
		int size = list.size();
		if (size <= 1) {
			return; // no need of permutation
		}
		for (int i = 0; i < size - 1; i++) {
			int j = rand.nextInt(size - i) + i;
			T vi = list.get(i);
			T vj = list.get(j);
			list.set(i, vj);
			list.set(j, vi);
		}
	}
}
