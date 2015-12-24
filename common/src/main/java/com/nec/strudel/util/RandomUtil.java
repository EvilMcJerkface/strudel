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
