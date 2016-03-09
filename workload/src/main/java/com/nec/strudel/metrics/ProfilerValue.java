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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.nec.strudel.json.JsonValues;

public final class ProfilerValue {
    private ProfilerValue() {
        // not instantiated
    }

    protected static Map<String, BigDecimal> jsonToMap(JsonObject json) {
        Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
        for (String name : json.keySet()) {
            BigDecimal value = json.getJsonNumber(name)
                    .bigDecimalValue();
            map.put(name, value);
        }
        return map;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static Combiner combiner() {
        return new Combiner();
    }

    public static Aggregator aggregator() {
        return new Aggregator();
    }

    public static class Builder {
        private final String name;
        private final JsonObjectBuilder jsonBuilder =
                Json.createObjectBuilder();

        Builder(String name) {
            this.name = name;
        }

        public Builder set(String name, long value) {
            jsonBuilder.add(name, value);
            return this;
        }

        public Builder set(String name, double value) {
            jsonBuilder.add(name, value);
            return this;
        }

        public Builder set(Map<String, ? extends Number> values) {
            for (Map.Entry<String, ? extends Number> e : values.entrySet()) {
                BigDecimal val = new BigDecimal(
                        e.getValue().toString());
                jsonBuilder.add(e.getKey(), val);
            }
            return this;
        }

        public JsonObject build() {
            return Json.createObjectBuilder()
                    .add(name, jsonBuilder.build())
                    .build();
        }

    }

    public static class Combiner {
        private final List<String> keys = new ArrayList<String>();
        private final Map<String, JsonValue> values = new HashMap<String, JsonValue>();

        public Combiner add(JsonObject valueSet) {
            for (Map.Entry<String, JsonValue> e : valueSet.entrySet()) {
                add(e.getKey(), e.getValue());
            }
            return this;
        }

        public Combiner add(String name, JsonValue value) {
            if (!values.containsKey(name)) {
                keys.add(name);
            }
            values.put(name, value);
            return this;
        }

        public Combiner add(String name, long value) {
            return add(name, JsonValues.toValue(value));
        }

        public JsonObject build() {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            for (String key : keys) {
                builder.add(key, values.get(key));
            }
            return builder.build();
        }
    }

    public static class Aggregator {
        private final Map<String, JsonObject> values = new HashMap<String, JsonObject>();

        public Aggregator add(JsonObject valueSets) {
            for (String key : valueSets.keySet()) {
                add(key, valueSets.getJsonObject(key));
            }
            return this;
        }

        private Aggregator add(String name, JsonObject value) {
            JsonObject value0 = values.get(name);
            if (value0 != null) {
                values.put(name,
                        add(value0, value));
            } else {
                values.put(name, value);
            }
            return this;
        }

        private JsonObject add(JsonObject v1, JsonObject v2) {
            Map<String, BigDecimal> map1 = jsonToMap(v1);
            Map<String, BigDecimal> map2 = jsonToMap(v2);
            for (Map.Entry<String, BigDecimal> e : map2.entrySet()) {
                BigDecimal v0 = e.getValue();
                BigDecimal val = map1.get(e.getKey());
                map1.put(e.getKey(),
                        (val != null ? v0.add(val) : v0));
            }
            JsonObjectBuilder builder = Json.createObjectBuilder();
            for (Map.Entry<String, BigDecimal> e : map1.entrySet()) {
                builder.add(e.getKey(), e.getValue());
            }
            return builder.build();
        }

        private JsonObject toJson(Map<String, JsonObject> values) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            for (Map.Entry<String, JsonObject> e : values.entrySet()) {
                builder.add(e.getKey(), e.getValue());
            }
            return builder.build();
        }

        public JsonObject build() {
            return toJson(values);
        }


    }

    public static JsonObject aggregate(JsonObject... values) {
        Aggregator aggr = new Aggregator();
        for (JsonObject v : values) {
            aggr.add(v);
        }
        return aggr.build();
    }

    public static JsonObject aggregate(Iterable<JsonObject> values) {
        Aggregator aggr = new Aggregator();
        for (JsonObject v : values) {
            aggr.add(v);
        }
        return aggr.build();
    }

    public static JsonObject combine(JsonObject... valueSets) {
        Combiner comb = new Combiner();
        for (JsonObject v : valueSets) {
            comb.add(v);
        }
        return comb.build();
    }

}
