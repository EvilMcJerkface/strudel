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

import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.nec.strudel.json.JsonValues;

public class Constant implements Func {
    public static Constant of(String value) {
        return new Constant(JsonValues.toValue(value));
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

    public static Constant number(String value) {
        return new Constant(JsonValues.number(value));
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
