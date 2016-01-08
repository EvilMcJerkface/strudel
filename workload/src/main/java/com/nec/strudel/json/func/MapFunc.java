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

import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

public class MapFunc implements Func {
	public static MapFunc of(Func func) {
		return new MapFunc(func);
	}
	public static Func of(Func func, Func argFunc) {
		return new Apply(new MapFunc(func), argFunc);
	}
	private final Func func;
	public MapFunc(Func func) {
		this.func = func;
	}

	@Override
	public JsonValue get(JsonValue... input) {
		JsonValue value =  input[0];
		if (ValueType.OBJECT == value.getValueType()) {
			return applyMap((JsonObject) value);
		} else if (ValueType.ARRAY == value.getValueType()) {
			return applyMap((JsonArray) value);
		} else {
			return func.get(value);
		}
	}
	JsonObject applyMap(JsonObject input) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		for (Map.Entry<String, JsonValue> e : input.entrySet()) {
			b.add(e.getKey(), func.get(e.getValue()));
		}
		return b.build();
	}
	JsonArray applyMap(JsonArray input) {
		JsonArrayBuilder b = Json.createArrayBuilder();
		for (JsonValue v : input) {
			b.add(func.get(v));
		}
		return b.build();
	}

	@Override
	public void output(JsonObjectBuilder out,
			String name, JsonValue... input) {
		out.add(name, get(input));
	}

}
