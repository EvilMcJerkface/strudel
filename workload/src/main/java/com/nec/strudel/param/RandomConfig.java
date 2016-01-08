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
package com.nec.strudel.param;

import java.util.List;

import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.util.Range;

public class RandomConfig {
	private String type;
	private String min;
	private String max;
	private String partition;
	private String mode;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public String getMax() {
		return max;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getPartition() {
		return partition;
	}
	public void setPartition(String partition) {
		this.partition = partition;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public RandomSelector<?> createSelector() {
		if (this.isPerm()) {
			return RandomSelector.createPermutation(intRange());
		} else {
			return RandomSelector.create(getRange());
		}
	}
	public RandomSelector<?> createSelector(int nid, int nodes) {
		if (this.isPerm()) {
			return RandomSelector.createPermutation(intRangeFor(nid, nodes));
		} else {
			return RandomSelector.create(rangeFor(nid, nodes));
		}
	}
	public RandomSelector<?>[] createSelectors(int nid, int nodes,
			int threads) {
		if (this.isPerm()) {
			return permutations(
					intRangeFor(nid, nodes)
					.partition(threads));
		} else {
			return selectors(
					rangeFor(nid, nodes)
					.partition(threads));
		}
	}
	public boolean isPerm() {
		return "perm".equals(mode);
	}
	public boolean threadPartition() {
		String opt = getPartition();
		if (opt == null) {
			return false;
		}
		if ("true".equalsIgnoreCase(opt)) {
			return true;
		}
		if ("thread".equalsIgnoreCase(opt)) {
			return true;
		}
		return false;

	}
	public boolean nodePartition() {
		if (threadPartition()) {
			return true;
		}
		String opt = getPartition();
		if (opt == null) {
			return false;
		}
		if ("node".equalsIgnoreCase(opt)) {
			return true;
		}
		return false;
		
	}
	public Range<Integer> intRange() {
		return Range.range(
				Integer.parseInt(min),
				Integer.parseInt(max));
	}

	public Range<Integer> intRangeFor(int nid, int nodes) {
		Range<Integer> range = intRange();
		if (nodePartition()) {
			return range.subRange(nid, nodes);
		} else {
			return range;
		}
	}

	public Range<?> getRange() {
		if ("int".equals(type)) {
			return intRange();
		} else if ("double".equals(type)) {
			return Range.range(
					Double.parseDouble(min),
					Double.parseDouble(max));
		}
		throw new ConfigException(
		        "unknown random type: " + type);
	}

	public Range<?> rangeFor(int nid, int nodes) {
		Range<?> range = getRange();
		if (nodePartition()) {
			return range.subRange(nid, nodes);
		} else {
			return range;
		}
	}
	public static <T> RandomSelector<?>[] selectors(List<Range<T>> ranges) {
		RandomSelector<?>[] rands = new RandomSelector<?>[ranges.size()];
		for (int i = 0; i < rands.length; i++) {
			rands[i] = RandomSelector.create(ranges.get(i));
		}
		return rands;
	}
	public static RandomSelector<?>[] permutations(List<Range<Integer>> ranges) {
		RandomSelector<?>[] rands = new RandomSelector<?>[ranges.size()];
		for (int i = 0; i < rands.length; i++) {
			rands[i] = RandomSelector.createPermutation(ranges.get(i));
		}
		return rands;
	}
}