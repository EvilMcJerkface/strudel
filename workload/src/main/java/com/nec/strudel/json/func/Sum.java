package com.nec.strudel.json.func;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import com.nec.strudel.json.JsonValues;

public class Sum implements Func {
	private static final Sum SUM = new Sum();
	public static Func of(Func... arg) {
		return new Apply(SUM, arg);
	}
	public Sum() {
	}

	@Override
	public JsonValue get(JsonValue... input) {
		if (input.length == 1) {
			return compute(input[0]);
		} else {
			return compute(input);
		}
	}
	JsonValue compute(JsonValue input) {
		if (input.getValueType() == ValueType.NUMBER) {
			return (JsonNumber) input;
		} else if (input.getValueType() == ValueType.OBJECT) {
			return compute((JsonObject) input);
		} else if (input == JsonValue.NULL) {
			return JsonValue.NULL;
		}
		throw new RuntimeException("unsupported type");
	}
	JsonValue compute(JsonObject input) {
		Number sum = Long.valueOf(0);
		for (JsonValue v : input.values()) {
			sum = add(sum, compute(v));
			if (sum == null) {
				return JsonValue.NULL;
			}
		}
		return JsonValues.toValue(sum);
	}

	/**
	 * TODO support vector + vector => vector
	 * (if values are all JsonObject, apply
	 * vector-wise addition and return JsonObject)
	 */
	JsonValue compute(JsonValue[] values) {
		Number sum = Long.valueOf(0);
		for (JsonValue v : values) {
			if (v == JsonValue.NULL) {
				return JsonValue.NULL;
			}
			sum = add(sum, compute(v));
			if (sum == null) {
				return JsonValue.NULL;
			}
		}
		return JsonValues.toValue(sum);
	}
	Number add(Number v, JsonValue v2) {
		if (v2 == JsonValue.NULL) {
			return null;
		}
		JsonNumber n = (JsonNumber) v2;
		if (n.isIntegral() && v instanceof Long) {
			return v.longValue() + n.longValue();
		} else {
			return v.doubleValue() + n.doubleValue();
		}
	}

	@Override
	public void output(JsonObjectBuilder out,
			String name, JsonValue... input) {
		out.add(name, get(input));
	}

}
