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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.nec.strudel.json.JsonValues;

public class Now implements Func {
    public static Func now() {
        return new Now();
    }

    public static Func now(String format) {
        return new Now(format);
    }

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz";
    private DateFormat dateFormat;

    public Now() {
        dateFormat = new SimpleDateFormat(DEFAULT_FORMAT);
    }

    public Now(String dateFormat) {
        this.dateFormat = new SimpleDateFormat(dateFormat);
    }

    @Override
    public JsonValue get(JsonValue... input) {
        return JsonValues.toValue(dateFormat.format(new Date()));
    }

    @Override
    public void output(JsonObjectBuilder out,
            String name, JsonValue... input) {
        out.add(name, get(input));
    }

}
