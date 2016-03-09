/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
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

package com.nec.strudel.workload.api;

import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

public class PopulateParam {
    private final Map<String, Object> values;
    private final int id;
    private final Random rand;

    public PopulateParam(int id, Map<String, Object> values, Random rand) {
        this.id = id;
        this.values = values;
        this.rand = rand;
    }

    public int getId() {
        return id;
    }

    @Nullable
    public Object get(String name) {
        return values.get(name);
    }

    @Nullable
    public Object get(Enum<?> name) {
        return get(name.name());
    }

    public int getInt(Enum<?> name) {
        return getInt(name.name());
    }

    public int getInt(String name) {
        Object val = get(name);
        if (val != null) {
            if (val instanceof Integer) {
                return (Integer) val;
            } else {
                return Integer.parseInt(val.toString());
            }
        } else {
            return 0;
        }
    }

    public double getDouble(Enum<?> name) {
        return getDouble(name.name());
    }

    public double getDouble(String name) {
        Object val = get(name);
        if (val != null) {
            if (val instanceof Double) {
                return (Double) val;
            } else {
                return Double.parseDouble(val.toString());
            }
        } else {
            return 0;
        }
    }

    public Random getRandom() {
        return rand;
    }
}
