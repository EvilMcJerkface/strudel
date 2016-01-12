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
package com.nec.strudel.workload.session.test.tool;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.nec.strudel.param.ParamSequence;

public class FixedLengthParamSequence implements ParamSequence {
	private final AtomicInteger remain;
	private final Map<String, Object> param;

	public FixedLengthParamSequence(int count, Map<String, Object> param) {
		this.remain = new AtomicInteger(count);
		this.param = Collections.unmodifiableMap(param);
	}

	@Override
	public Map<String, Object> nextParam(Random rand) {
		if (remain.getAndDecrement() > 0) {
			return param;
		} else {
			return null;
		}
	}

}
