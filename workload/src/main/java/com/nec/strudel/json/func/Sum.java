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
     * TODO support vector + vector => vector (if values are all JsonObject,
     * apply vector-wise addition and return JsonObject)
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

    Number add(Number numberValue, JsonValue value) {
        if (value == JsonValue.NULL) {
            return null;
        }
        JsonNumber number = (JsonNumber) value;
        if (number.isIntegral() && numberValue instanceof Long) {
            return numberValue.longValue() + number.longValue();
        } else {
            return numberValue.doubleValue() + number.doubleValue();
        }
    }

    @Override
    public void output(JsonObjectBuilder out,
            String name, JsonValue... input) {
        out.add(name, get(input));
    }

}
