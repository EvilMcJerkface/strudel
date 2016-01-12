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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.InteractionFactory;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;
import com.nec.strudel.workload.session.FactoryUtil;
import com.nec.strudel.workload.session.Name;

public class Factory implements InteractionFactory<Object> {
	static final Map<String, Interaction<Object>> INTRS =
		new HashMap<String, Interaction<Object>>();
	static final void def(Interaction<Object> intr) {
		INTRS.put(FactoryUtil.getName(intr), intr);
	}
	static {
		def(new Intr0());
		def(new Intr1());
		def(new Intr2());
		def(new Intr3());
	}
	@Override
	public Interaction<Object> create(String name) {
		return INTRS.get(name);
	}
	@Override
	public Set<String> names() {
		return Collections.unmodifiableSet(INTRS.keySet());
	}
	public abstract static class TestIntr implements Interaction<Object> {
		
	}
	@Name("I0")
	public static class Intr0 extends TestIntr {

		@Override
		public void prepare(ParamBuilder builder) {
			
		}

		@Override
		public Result execute(Param param, Object db, ResultBuilder res) {
			return res.success();
		}

		@Override
		public void complete(StateModifier modifier) {
		}
		
	}


	@Name("I1")
	public static class Intr1 extends TestIntr {


		@Override
		public void prepare(ParamBuilder builder) {
			
		}

		@Override
		public Result execute(Param param, Object db, ResultBuilder res) {
			return res.success();
		}

		@Override
		public void complete(StateModifier modifier) {
		}
		
	}
	@Name("I2")
	public static class Intr2 extends TestIntr {

		@Override
		public void prepare(ParamBuilder builder) {
			
		}

		@Override
		public Result execute(Param param, Object db, ResultBuilder res) {
			return res.success();
		}

		@Override
		public void complete(StateModifier modifier) {
		}
		
	}
	@Name("I3")
	public static class Intr3  extends TestIntr {

		@Override
		public void prepare(ParamBuilder builder) {
			
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
