package com.nec.strudel.json.func;

import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.nec.strudel.json.JsonValues;

public class Constant implements Func {
	public static Constant of(String value) {
		return new Constant(JsonValues.toValue(value));
	}
	public static Constant number(String value) {
		return new Constant(JsonValues.number(value));
	}
	public static Constant of(long value) {
		return new Constant(JsonValues.toValue(value));
	}
	public static Constant of(double value) {
		return new Constant(JsonValues.toValue(value));
	}
	public static Constant of(JsonValue value) {
		return new Constant(value);
	}

	private final JsonValue value;
	public Constant(JsonValue value) {
		this.value = value;
	}

	@Override
	public JsonValue get(JsonValue... input) {
		return value;
	}

	@Override
	public void output(JsonObjectBuilder out,
			String name, JsonValue... input) {
		out.add(name, value);
	}
}
