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
    private static final JsonObject EMPTY_OBJ = Json.createObjectBuilder()
            .build();

    private JsonValues() {
    }

    public static JsonObject union(JsonObject... values) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (JsonObject v : values) {
            for (Map.Entry<String, JsonValue> e : v.entrySet()) {
                builder.add(e.getKey(), e.getValue());
            }
        }
        return builder.build();
    }

    public static JsonObject union(Iterable<JsonObject> values) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (JsonObject v : values) {
            for (Map.Entry<String, JsonValue> e : v.entrySet()) {
                builder.add(e.getKey(), e.getValue());
            }
        }
        return builder.build();
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
            Double doubleValue = (Double) obj;
            if (Double.isNaN(doubleValue)) {
                return null;
            } else {
                return JsonValueUtil.create(doubleValue);
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
            Double doubleValue = (Double) obj;
            if (Double.isNaN(doubleValue)) {
                return JsonValue.NULL;
            } else {
                return JsonValueUtil.create(doubleValue);
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
