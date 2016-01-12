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

import java.util.concurrent.atomic.AtomicInteger;

import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;
import com.nec.strudel.session.StateParam;

public class IncrementCount implements Interaction<AtomicInteger> {
	public enum TestParam implements StateParam {
		COUNT;
	}

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		paramBuilder.use(TestParam.COUNT);
	}

	@Override
	public Result execute(Param param, AtomicInteger db, ResultBuilder res) {
		int count = param.getInt(TestParam.COUNT);
		res.set(TestParam.COUNT, count + 1);
		db.incrementAndGet();
		return res.success();
	}

	@Override
	public void complete(StateModifier modifier) {
		modifier.export(TestParam.COUNT);
	}
	
}
