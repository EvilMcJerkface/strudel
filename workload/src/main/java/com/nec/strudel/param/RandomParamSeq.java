package com.nec.strudel.param;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.util.RandomSelector;

/**
 * A sequence of a parameter set, some of whose parameters
 * are chosen randomly by RandomSelector.
 * @author tatemura
 *
 */
@ThreadSafe
public class RandomParamSeq implements ParamSequence {
	private final Map<String, Object> constants;
	private final Map<String, RandomSelector<?>> rands;

	public RandomParamSeq(Map<String, Object> constants,
			Map<String, RandomSelector<?>> rands) {
		this.constants = constants;
		this.rands = rands;
	}

	@Override
	public Map<String, Object> nextParam(Random rand) {
		Map<String, Object> map =
			new HashMap<String, Object>(constants);
		for (Map.Entry<String, RandomSelector<?>> e
		        : rands.entrySet()) {
			map.put(e.getKey(), e.getValue().next(rand));
		}
		return map;
	}

}
