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
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.nec.strudel.json.func.Func;

public class ResultQuery {
	public static ResultQuery query() {
		return new ResultQuery();
	}
	private final List<Output> outputs =
			new ArrayList<Output>();
	public ResultQuery() {
	}
	public ResultQuery output(Output... outs) {
		for (Output out : outs) {
			outputs.add(out);
		}
		return this;
	}
	public JsonObject execute(JsonObject source) {
		JsonObjectBuilder dst = Json.createObjectBuilder();
		for (Output out : outputs) {
			for (Map.Entry<String, Func> e : out.entries()) {
				e.getValue().output(dst, e.getKey(), source);
			}
		}
		return dst.build();
	}

}
