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
package com.nec.strudel.bench.test.interactions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;

import com.nec.strudel.bench.test.AbstractEntityTestBase;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.StateParam;
import com.nec.strudel.session.impl.ParamBuilderImpl;
import com.nec.strudel.session.impl.State;
import com.nec.strudel.session.impl.StateModifierImpl;
import com.nec.strudel.util.ClassUtil;

public class AbstractInteractionTestBase extends AbstractEntityTestBase {

	@SuppressWarnings("unchecked")
	protected Interaction<EntityDB> interaction() {
		TestOn testOn = this.getClass().getAnnotation(TestOn.class);
		if (testOn != null) {
			return (Interaction<EntityDB>) ClassUtil.create(
					testOn.value());
		} else {
			Assert.fail("@TestOn is missing in this test: "
					+ this.getClass());
			return null;
		}
	}

	protected Param prepare(State state) {
		Param param = new Param();
		interaction().prepare(new ParamBuilderImpl(state, param));
		return param;
	}
	protected State complete(Result res) {
		return completer(res).complete();
	}
	protected State newState() {
        Random rand = new Random();
        Map<String, Object> map = new HashMap<String, Object>();
        State state = State.newState(map, rand);
        return state;
	}
	protected Executor<EntityDB> executor() {
		return new Executor<EntityDB>(interaction(), getDb());
	}
	protected Completer completer(Result res) {
		return new Completer(interaction(), res);
	}

	protected static class Completer {
		private final Interaction<?> intr;
		private State state;
		private Result res;
		Completer(Interaction<?> intr, Result res) {
			this.intr = intr;
			this.res = res;
	        Random rand = new Random();
	        Map<String, Object> map = new HashMap<String, Object>();
	        this.state = State.newState(map, rand);
		}
		
		public Completer state(StateParam name, Object value) {
			state.put(name, value);
			return this;
		}
		public State complete() {
			intr.complete(
					new StateModifierImpl(res, state));
			return state;
		}
	}
}
