package com.nec.strudel.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.concurrent.Immutable;

/**
 *
 * @param <V>
 */
@Immutable
public abstract class Range<V> {
	/**
	 * gets MIN value (inclusive)
	 */
	public abstract V min();
	/**
	 * gets MAX value (exclusive)
	 */
	public abstract V max();
	/**
	 * Gets a random value within
	 * the range.
	 */
	public abstract V next(Random rand);

	/**
	 * Gets k-th sub-range in the N-partition
	 * of the range (k = 0,..., N-1)
	 * @param idx the position (k) of a sub-range
	 * in the list of partitions.
	 * @param partition the number (N) of the partitions.
	 * @return a range
	 */
	public abstract Range<V> subRange(int idx, int partition);

	public List<Range<V>> partition(int partition) {
		List<Range<V>> ranges = new ArrayList<Range<V>>(partition);
		for (int i = 0; i < partition; i++) {
			ranges.add(subRange(i, partition));
		}
		return ranges;
	}

	public static IntRange range(int min, int max) {
		return new IntRange(min, max);
	}
	public static DoubleRange range(double min, double max) {
		return new DoubleRange(min, max);
	}
	public static class IntRange extends Range<Integer> {
		private final int min;
		private final int max;
		public IntRange(int min, int max) {
			this.min = min;
			this.max = max;
			if (min >= max) {
				throw new IllegalArgumentException(
					"invalid range: [" + min + "," + max + ")");
			}
		}
		@Override
		public Integer min() {
			return min;
		}
		

		@Override
		public Integer max() {
			return max;
		}

		@Override
		public Integer next(Random rand) {
			if (min == max) {
				return min;
			}
			return rand.nextInt(max - min) + min;
		}

		@Override
		public IntRange subRange(int idx, int partition) {
			if (idx < 0 || idx >= partition) {
				throw new IllegalArgumentException(
					"partition index out of range: " + idx);
			}
			int size = (max - min) / partition;
			int start = min + size * idx;
			if (idx + 1 == partition) { // last
				return new IntRange(start, max);
			}
			return new IntRange(start, start + size);
		}
		@Override
		public String toString() {
			return "[" + min + "," + (max - 1) + "]";
		}
	}
	public static class DoubleRange extends Range<Double> {
		private final double min;
		private final double max;
		public DoubleRange(double min, double max) {
			this.min = min;
			this.max = max;
			if (min >= max) {
				throw new IllegalArgumentException(
					"invalid range: [" + min + "," + max + ")");
			}
		}
		@Override
		public Double min() {
			return min;
		}
		@Override
		public Double max() {
			return max;
		}
		@Override
		public Double next(Random rand) {
			return min + rand.nextDouble() * (max - min);
		}
		@Override
		public DoubleRange subRange(int idx, int partition) {
			if (idx < 0 || idx >= partition) {
				throw new IllegalArgumentException(
					"partition index out of range: " + idx);
			}
			double size = (max - min) / partition;
			double start = min + size * idx;
			if (idx + 1 == partition) { // last
				return new DoubleRange(start, max);
			}
			return new DoubleRange(start, start + size);
		}
		@Override
		public String toString() {
			return "[" + min + "," + max + ")";
		}
	}
}