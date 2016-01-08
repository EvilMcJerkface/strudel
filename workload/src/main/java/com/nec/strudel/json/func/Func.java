package com.nec.strudel.json.func;

import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public interface Func {

	JsonValue get(JsonValue... input);
	void output(JsonObjectBuilder out, String name, JsonValue... input);
}
