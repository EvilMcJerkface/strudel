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

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * Gets the value that is associated with
 * the specified key in a JsonObject, which
 * is given as the argument.
 * returns JsonValue.NULL if there is no such a key.
 * @author tatemura
 *
 */
public class Value implements Func {
	public static Func of(String name) {
		return new Value(name);
	}
	public static Func path(String... path) {
		return new Value(path);
	}
	public static Func of(String name, Func arg) {
		return new Apply(new Value(name), arg);
	}
	private final String[] path;
	public Value(String... path) {
		this.path = path;
	}

	@Override
	public JsonValue get(JsonValue... input) {
		JsonValue value =  input[0];
		for (int i = 0; i < path.length; i++) {
			String name = path[i];
			value = ((JsonObject) value).get(name);
			if (value == null) {
				return JsonValue.NULL;
			}
		}
		return value;
	}

	@Override
	public void output(JsonObjectBuilder out, String name,
			JsonValue... input) {
		JsonValue value = get(input);
		if (value != JsonValue.NULL) {
			out.add(name, value);
		}
	}

}
