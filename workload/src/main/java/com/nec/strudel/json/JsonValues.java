package com.nec.strudel.json;

import java.util.Map;

import javax.annotation.Nullable;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.nec.congenio.json.JsonValueUtil;

public final class JsonValues {
	private final static JsonObject EMPTY_OBJ = Json.createObjectBuilder().build();

	private JsonValues() {
	}

	public static JsonObject union(JsonObject... values) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		for (JsonObject v : values) {
			for (Map.Entry<String, JsonValue> e : v.entrySet()) {
				b.add(e.getKey(), e.getValue());
			}
		}
		return b.build();
	}
	public static JsonObject union(Iterable<JsonObject> values) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		for (JsonObject v : values) {
			for (Map.Entry<String, JsonValue> e : v.entrySet()) {
				b.add(e.getKey(), e.getValue());
			}
		}
		return b.build();
	}
	public static JsonObject emptyObject() {
		return EMPTY_OBJ;
	}
	public static JsonNumber number(String value) {
		return JsonValueUtil.number(value);
	}
	@Nullable
	public static JsonNumber toNumber(Object obj) {
		if (obj instanceof Double) {
			Double d = (Double) obj;
			if (Double.isNaN(d)) {
				return null;
			} else {
				return JsonValueUtil.create(d);
			}
		}
		if (obj instanceof Number) {
			return JsonValueUtil.create((Number) obj);
		} else {
			return JsonValueUtil.number(obj.toString());
		}
	}
	public static JsonValue toValue(Object obj) {
		if (obj instanceof Double) {
			Double d = (Double) obj;
			if (Double.isNaN(d)) {
				return JsonValue.NULL;
			} else {
				return JsonValueUtil.create(d);
			}
		}
		if (obj instanceof Number) {
			return JsonValueUtil.create((Number) obj);
		} else if (obj instanceof JsonValue) {
			return (JsonValue) obj;
		} else {
			return JsonValueUtil.create(obj.toString());
		}
	}
}
