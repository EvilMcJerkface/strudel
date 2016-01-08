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
