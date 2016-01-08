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
