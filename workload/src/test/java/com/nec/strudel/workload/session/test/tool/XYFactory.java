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

import java.util.HashSet;
import java.util.Set;

import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.InteractionFactory;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;
import com.nec.strudel.session.StateParam;
import com.nec.strudel.workload.test.kvmap.KVMap;

public class XYFactory implements InteractionFactory<KVMap> {
	enum StateVar implements StateParam {
		XIDS,
		YIDS,
	}
	enum Var implements LocalParam {
		KEY,
		VALUE
	}
	enum Interactions {
		ADD_X(new AddX()),
		ADD_Y(new AddY());
		private final Interaction<KVMap> intr;
		Interactions(Interaction<KVMap> intr) {
			this.intr = intr;
		}
		public Interaction<KVMap> getIntraction() {
			return intr;
		}
	};
	@Override
	public Interaction<KVMap> create(String name) {
		Interactions intr = Interactions.valueOf(name);
		if (intr != null) {
			return intr.getIntraction();
		}
		return null;
	}

	@Override
	public Set<String> names() {
		Set<String> names = new HashSet<String>();
		for (Interactions intr : Interactions.values()) {
			names.add(intr.name());
		}
		return names;
	}
	static class AddX implements Interaction<KVMap> {

		@Override
		public void prepare(ParamBuilder builder) {
			int num = builder.getInt(StateVar.XIDS);
			String key = "x" + builder.getRandomInt(num);
			builder
			.set(Var.KEY, key)
			.set(Var.VALUE, builder.getRandomInt(10) + 1);
		}

		@Override
		public Result execute(Param param, KVMap db, ResultBuilder res) {
			db.add(param.get(Var.KEY), param.getInt(Var.VALUE));
			return res.success();
		}

		@Override
		public void complete(StateModifier modifier) {
		}
		
	}
	static class AddY implements Interaction<KVMap> {

		@Override
		public void prepare(ParamBuilder builder) {
			int num = builder.getInt(StateVar.YIDS);
			String key = "y" + builder.getRandomInt(num);
			builder.set(Var.KEY, key)
			.set(Var.VALUE, builder.getRandomInt(10) + 1);
		}

		@Override
		public Result execute(Param param, KVMap db, ResultBuilder res) {
			db.add(param.get(Var.KEY), param.getInt(Var.VALUE));
			return res.success();
		}

		@Override
		public void complete(StateModifier modifier) {
		}
	}
}
