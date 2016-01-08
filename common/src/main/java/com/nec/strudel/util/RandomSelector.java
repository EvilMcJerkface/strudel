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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class RandomSelector<T> {


	public static <T> RandomSelector<T> create(Map<T, Double> prob) {
		double sum = 0;
		for (Double p : prob.values()) {
			sum += p;
		}
		List<T> items = new ArrayList<T>(prob.size());
		double[] cdf = new double[prob.size()];
		int idx = 0;
		double cum = 0;
		for (Map.Entry<T, Double> e : prob.entrySet()) {
			cum += e.getValue() / sum;
			T key = e.getKey();
			items.add(key);
			cdf[idx] = cum;
			idx++;
		}
		return new NonUniformRandomSelector<T>(items, cdf);
	}
	public static <T> RandomSelector<T> create(List<T> items) {
		return new UniformRandomSelector<T>(items);
	}
	public static <T> RandomSelector<T> constant(T value) {
		return new ConstantSelector<T>(value);
	}
	/**
	 * A selector that uniformly chooses an integer from a given range.
	 * @param min the lower bound of the range (inclusive)
	 * @param max the upper bound of the range (exclusive)
	 */
	public static RandomSelector<Integer> create(int min, int max) {
		return new UniformIntegerSelector(min, max);
	}
	public static <T> RandomSelector<T> create(Range<T> range) {
		return new UniformRangeSelector<T>(range);
	}
	public static RandomSelector<Integer> createPermutation(
			int min, int max) {
		return new IntegerPermutation(min, max);
	}
	public static RandomSelector<Integer> createPermutation(
			Range<Integer> range) {
		return new IntegerPermutation(range.min(), range.max());
	}

	public static RandomSelector<Double> createDouble(
	        double min, double max) {
		return new UniformDoubleSelector(min, max);
	}
	public static RandomSelector<Boolean> createBoolean(double prob) {
		return new BooleanSelector(prob);
	}
	public static RandomSelector<Integer> integerExcept(int min, int max,
			int except) {
		if (max - min == 1) {
			/**
			 * NOTE This is the case of testing
			 * where there is only one item
			 */
			return constant(min);
		}
		return new OtherIntegers(min, max, except);
	}
	public static RandomSelector<String> createAlphaString(int length) {
		return new AlphaStringSelector(length);
	}

	public abstract T next(Random r);

	static class UniformRandomSelector<T> extends RandomSelector<T> {
		private final List<T> items;
		UniformRandomSelector(List<T> items) {
			this.items = items;
		}
		@Override
		public T next(Random r) {
			int idx = r.nextInt(items.size());
			return items.get(idx);
		}
	}
	static class NonUniformRandomSelector<T> extends RandomSelector<T> {
		private final List<T> items;
		private final double[] cumulative;
		NonUniformRandomSelector(List<T> items, double[] cumulative) {
			this.items = items;
			this.cumulative = cumulative;
		}
		@Override
		public T next(Random r) {
			double v = r.nextDouble();
			for (int i = 0; i < cumulative.length; i++) {
				if (v < cumulative[i]) {
					return items.get(i);
				}
			}
			return items.get(items.size() - 1);
		}
	}
	static class UniformIntegerSelector extends RandomSelector<Integer> {
		private final int n;
		private final int min;
		UniformIntegerSelector(int min, int max) {
			this.n = max - min;
			this.min = min;
		}
		@Override
		public Integer next(Random r) {
			if (n == 0) {
				// min = max, just return min - Oliver
				return min;
			}
			return r.nextInt(n) + min;
		}
	}
	static class UniformRangeSelector<T> extends RandomSelector<T> {
		private final Range<T> range;
		public UniformRangeSelector(Range<T> range) {
			this.range = range;
		}
		@Override
		public T next(Random r) {
			return range.next(r);
		}
	}
	static class UniformDoubleSelector extends RandomSelector<Double> {
		private final double min;
		private final double range;
		public UniformDoubleSelector(double min, double max) {
			this.range = max - min;
			this.min = min;
		}
		@Override
		public Double next(Random r) {
			return min + r.nextDouble() * range;
		}
	}
	static class BooleanSelector extends RandomSelector<Boolean> {
		private final double prob;
		public BooleanSelector(double prob) {
			this.prob = prob;
		}
		@Override
		public Boolean next(Random r) {
			return r.nextDouble() < prob;
		}
	}
	static class OtherIntegers extends RandomSelector<Integer> {
		private final RandomSelector<Integer> range;
		private final int except;
		private final int valForExcept;
		OtherIntegers(int min, int max, int except) {
			this.except = except;
			if (min <= except && except < max) {
				/**
				 * swap the except value and the last
				 * value in the range
				 */
				this.valForExcept = max - 1;
				this.range =
					RandomSelector.create(min, max - 1);
			} else { // except is not in the range
				this.range = RandomSelector.create(min, max);
				this.valForExcept = 0; // don't care
			}
		}
		@Override
		public Integer next(Random r) {
			int value = range.next(r);
			if (value == except) {
				return valForExcept;
			}
			return value;
		}
	}

	static class IntegerPermutation extends RandomSelector<Integer> {
		private final int[] values;
		private int idx = 0;
		public IntegerPermutation(int min, int max) {
			values = new int[max - min];
			for (int i = 0; i < values.length; i++) {
				values[i] = min + i;
			}
		}
		@Override
		public synchronized Integer next(Random r) {
			int pos = r.nextInt(values.length - idx) + idx;
			int v = values[pos];
			values[pos] = values[idx];
			values[idx] = v; // not necessary...
			idx++;
			if (idx >= values.length) {
				idx = 0;
			}
			return v;
		}
	}

	static class ConstantSelector<T> extends RandomSelector<T> {
		private final T value;
		ConstantSelector(T value) {
			this.value = value;
		}
		@Override
		public T next(Random r) {
			return value;
		}
	}
	static class AlphaSelector extends RandomSelector<Character> {
		@Override
		public Character next(Random r) {
			char c;
			if (r.nextBoolean()) {
				c = (char) ('A' + r.nextInt('Z' - 'A'));
			} else {
				c = (char) ('a' + r.nextInt('z' - 'a'));
			}
			return new Character(c);
		}
	}
	static class AlphaStringSelector extends StringSelector {
		public AlphaStringSelector(int length) {
			super(length, new AlphaSelector());
		}
	}
	static class StringSelector extends RandomSelector<String> {
		private final int length;
		private final RandomSelector<Character> chars;
		public StringSelector(int length,
				RandomSelector<Character> chars) {
			this.length = length;
			this.chars = chars;
		}
		@Override
		public String next(Random r) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < length; i++) {
				sb.append(chars.next(r).charValue());
			}
			return sb.toString();
		}
	}
}
