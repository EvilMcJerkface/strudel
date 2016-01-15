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
package com.nec.strudel.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * Data object that encapsulates metric values (JsonObject)
 * and warning messages (a list of String values).
 * @author tatemura
 *
 */
public class Report {
	public static final String VALUE_COUNTERS = "Values";
	public static final String VALUE_WARNS = "Warnings";

	private final JsonObject values;
	private final List<String> warns;

	protected Report(JsonObject values, List<String> warns) {
		this.values = values;
		this.warns = warns;
	}

	public Collection<String> getWarns() {
		return warns;
	}

	/**
	 * Converts the entire report to Json
	 * @return a JsonObject that contains
	 * values and warns.
	 */
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (!values.isEmpty()) {
			builder.add(VALUE_COUNTERS, getValues());
		}
		if (!warns.isEmpty()) {
			JsonArrayBuilder b = Json.createArrayBuilder();
			for (String w : warns) {
				b.add(w);
			}
			builder.add(VALUE_WARNS, b);
		}
		return builder.build();
	}
	/**
	 * Gets aggregated metric values
	 * @return JsonObject that contains
	 * measured values
	 */
	public JsonObject getValues() {
		return values;
	}

	public static Report toReport(JsonObject json) {
		List<String> warns =
    			new ArrayList<String>();

		JsonObject c = json.getJsonObject(VALUE_COUNTERS);
		if (c == null) {
			c = EMPTY_VALUE;
		}
		JsonArray ws = json.getJsonArray(VALUE_WARNS);
		if (ws != null) {
			for (JsonValue w : ws) {
				warns.add(w.toString());
			}
		}
		return new Report(c, warns);
	}
	/**
	 * Combines multiple reports into one
	 * by:
	 * <ul>
	 * <li> aggregating Count
	 * <li> concatenating lists of warnings.
	 * </ul>
	 * This is used to combine reports from multiple
	 * work threads / work nodes.
	 * @param reports
	 */
	public static Report aggregate(Report... reports) {
		ProfilerValue.Aggregator agg = ProfilerValue.aggregator();
		List<String> warns =
    			new ArrayList<String>();
		for (Report r : reports) {
			if (r != null) {
				agg.add(r.getValues());
				warns.addAll(r.getWarns());
			}
		}
		return new Report(agg.build(), warns);
	}

    public static Report none() {
    	return new Report(EMPTY_VALUE,
    			EMPTY_WARNS);
    }
    public static Report report(JsonObject value) {
    	return new Report(value, EMPTY_WARNS);
    }
    public static Report report(JsonObject value, List<String> messages) {
		return new Report(value, messages);
    }
    public static Report warn(List<String> messages) {
    	return new Report(EMPTY_VALUE, messages);
    }

    private static final JsonObject EMPTY_VALUE = Json.createObjectBuilder().build();
    private static final List<String> EMPTY_WARNS = Collections.emptyList();


}
