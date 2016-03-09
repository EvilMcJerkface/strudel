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

package com.nec.strudel.workload.measure;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.nec.strudel.json.JsonValues;
import com.nec.strudel.json.func.Now;

public class ResultAggregationFactory {
    public static final String TYPE_AVG = "avg";
    public static final String TYPE_ARRAY = "array";
    public static final String TYPE_TIMED = "timed";
    private String type;

    public ResultAggregationFactory(String type) {
        this.type = type;
    }

    public ResultAggregation create() {
        if (TYPE_ARRAY.equals(type)) {
            return new Array();
        } else if (TYPE_TIMED.equals(type)) {
            return new TimedArray();
        }
        return new Avg();
    }

    public static class TimedArray implements ResultAggregation {
        private JsonArrayBuilder builder = Json.createArrayBuilder();
        private Now now = new Now();

        @Override
        public void clear() {
            builder = Json.createArrayBuilder();
        }

        @Override
        public void put(JsonObject value) {
            builder.add(Json.createObjectBuilder()
                    .add("time", now.get())
                    .add("value", value)
                    .build());
        }

        @Override
        public JsonValue get() {
            return builder.build();
        }
    }

    public static class Array implements ResultAggregation {
        private JsonArrayBuilder builder = Json.createArrayBuilder();

        @Override
        public void clear() {
            builder = Json.createArrayBuilder();
        }

        @Override
        public void put(JsonObject value) {
            builder.add(value);
        }

        @Override
        public JsonValue get() {
            return builder.build();
        }
    }

    public static class Avg implements ResultAggregation {
        private int count;
        private Map<String, JsonValue> sum = new HashMap<String, JsonValue>();

        @Override
        public void clear() {
            count = 0;
            sum.clear();
        }

        @Override
        public void put(JsonObject value) {
            count += 1;
            for (String k : value.keySet()) {
                JsonValue v1 = sum.get(k);
                if (v1 == null) {
                    sum.put(k, value.get(k));
                } else {
                    sum.put(k, add(v1, value.get(k)));
                }
            }
        }

        @Override
        public JsonValue get() {
            if (count == 0) {
                return JsonValue.NULL;
            }
            JsonObjectBuilder builder = Json.createObjectBuilder();
            for (Map.Entry<String, JsonValue> e : sum.entrySet()) {
                builder.add(e.getKey(),
                        div(e.getValue(), count));
            }
            return builder.build();
        }
    }

    static JsonValue add(JsonValue v1, JsonValue v2) {
        if (v1 instanceof JsonArray) {
            return add((JsonArray) v1, (JsonArray) v2);
        }
        if (v1 == JsonValue.NULL || v2 == JsonValue.NULL) {
            return JsonValue.NULL;
        }
        JsonNumber num1 = (JsonNumber) v1;
        JsonNumber num2 = (JsonNumber) v2;
        return JsonValues.toValue(
                num1.bigDecimalValue().add(num2.bigDecimalValue()));
    }

    static JsonArray add(JsonArray a1, JsonArray a2) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        int size = Math.max(a1.size(), a2.size());
        for (int i = 0; i < size; i++) {
            JsonValue v1 = (i < a1.size() ? a1.get(i) : null);
            JsonValue v2 = (i < a2.size() ? a2.get(i) : null);
            if (v1 == null) {
                builder.add(v2);
            } else if (v2 == null) {
                builder.add(v1);
            } else {
                builder.add(add(v1, v2));
            }
        }
        return builder.build();
    }

    static JsonValue div(JsonValue v1, int v2) {
        if (v2 == 1) {
            return v1;
        }
        if (v1 instanceof JsonArray) {
            return div((JsonArray) v1, v2);
        }
        if (v1 == JsonValue.NULL) {
            return JsonValue.NULL;
        }
        JsonNumber num1 = (JsonNumber) v1;
        double val = num1.doubleValue() / v2;
        return JsonValues.toValue(val);
    }

    static JsonArray div(JsonArray a1, int v2) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (JsonValue v : a1) {
            builder.add(div(v, v2));
        }
        return builder.build();
    }
}
