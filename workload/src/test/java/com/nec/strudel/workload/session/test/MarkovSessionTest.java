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
package com.nec.strudel.workload.session.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;
import com.nec.strudel.session.impl.State;
import com.nec.strudel.workload.session.MarkovSession;
import com.nec.strudel.workload.session.MarkovStateModel;
import com.nec.strudel.workload.session.UserAction;
import com.nec.strudel.workload.session.WaitTime;

public class MarkovSessionTest {

	@Test
	public void testMaxCount() {
		WaitTime wait = noWait();
		MarkovStateModel<Object> msm = singleStateModel();
		for (int maxCount = 0; maxCount <= 10; maxCount++) {
			MarkovSession<Object> session = new MarkovSession<Object>(maxCount, 0, 0, msm, wait);
			State state = newState();
			int count = -1;
			for (int i = 0; i < maxCount + 10; i++) {
				UserAction<Object> action =
						session.next(state);
				if (action == null) {
					count = i;
					break;
				}
			}
			if (maxCount == 0) {
				// next must not return null
				assertEquals(-1, count);
			} else {
				assertEquals(maxCount, count);
			}
		}
	}

	State newState() {
		Map<String, Object> values = new HashMap<String, Object>();
		return State.newState(values, new Random());
	}
	WaitTime noWait() {
		return new WaitTime.Builder().build();
	}
	MarkovStateModel<Object> singleStateModel() {
		String name = "test";
		MarkovStateModel.Builder<Object> builder =
				new MarkovStateModel.Builder<Object>();
		builder.state(name, new NopInteraction());
		Map<String, Double> nexts = new HashMap<String, Double>();
		nexts.put(name, 1.0);
		builder.transition(name, nexts);
		builder.transition("START", nexts);
		return builder.build();
	}
	static class NopInteraction implements Interaction<Object> {

		@Override
		public void prepare(ParamBuilder paramBuilder) {
		}

		@Override
		public Result execute(Param param, Object db, ResultBuilder res) {
			return res.success();
		}

		@Override
		public void complete(StateModifier modifier) {
		}
		
	}
}
