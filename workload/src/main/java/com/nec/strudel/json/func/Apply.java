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
package com.nec.strudel.json.func;

import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class Apply implements Func {
	private final Func func;
	private final Func[] argFunc;
	public Apply(Func func, Func... argFunc) {
		this.func = func;
		this.argFunc = argFunc;
	}

	@Override
	public JsonValue get(JsonValue... input) {
		return func.get(computeArg(input));
	}

	JsonValue[] computeArg(JsonValue... input) {
		JsonValue[] args = new JsonValue[argFunc.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = argFunc[i].get(input);
		}
		return args;
	}

	@Override
	public void output(JsonObjectBuilder out,
			String name, JsonValue... input) {
		func.output(out, name, computeArg(input));
	}

}
