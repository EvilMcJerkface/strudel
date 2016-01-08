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
package com.nec.strudel.workload.out;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;
import javax.json.JsonValue;

import com.nec.strudel.instrument.impl.NamedFunc;
import com.nec.strudel.json.func.Constant;
import com.nec.strudel.json.func.Func;
import com.nec.strudel.json.func.Value;

public class Output {
	private final List<Map.Entry<String, Func>> outs;

	public Output(List<Map.Entry<String, Func>> outs) {
		this.outs = outs;
	}

	public Iterable<Map.Entry<String, Func>> entries() {
		return outs;
	}

	public static Builder builder() {
		return new Builder();
	}
	public static Output empty() {
		return builder().build();
	}
	public static Output referenceTo(JsonObject values) {
		Output.Builder b = builder();
		for (String key : values.keySet()) {
			b.add(key);
		}
		return b.build();
	}

	public static Output valueOf(JsonObject values) {
		Output.Builder b = builder();
		for (String key : values.keySet()) {
    		JsonValue value = values.get(key);
    		b.add(key, value);	
		}
		return b.build();
	}
	public static Output names(String... names) {
		Output.Builder b = builder();
		for (String name : names) {
			b.add(name);
		}
		return b.build();
	}
	public static Output funcs(Collection<? extends Map.Entry<String, Func>> funcs) {
		return builder().add(funcs).build();
	}

	public String toString() {
		return outs.toString();
	}

	public static class Builder {

		private final List<Map.Entry<String, Func>> outs =
				new ArrayList<Map.Entry<String, Func>>();

		public Builder add(Output out) {
			outs.addAll(out.outs);
			return this;
		}

		public Builder add(String name, Func f) {
			outs.add(new NamedFunc(name, f));
			return this;
		}
		public Builder add(String name) {
			return add(name, Value.of(name));
		}
		public Builder add(String name, JsonValue value) {
			return add(name, Constant.of(value));
		}
		public Builder add(Collection<? extends Map.Entry<String, Func>> funcs) {
			outs.addAll(funcs);
			return this;
		}

		public Output build() {
			return new Output(outs);
		}

	}
}
