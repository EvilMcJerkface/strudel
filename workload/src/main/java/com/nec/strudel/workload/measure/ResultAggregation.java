package com.nec.strudel.workload.measure;

import javax.json.JsonObject;
import javax.json.JsonValue;

public interface ResultAggregation {
	void clear();
	/**
	 * puts one monitoring result
	 * @param value a key value set of the
	 * monitored values.
	 */
	void put(JsonObject value);
	JsonValue get();
}