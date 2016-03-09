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

package com.nec.strudel.instrument.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;
import javax.json.Json;
import javax.json.JsonObject;

import com.nec.strudel.instrument.CountInstrument;
import com.nec.strudel.instrument.Profiler;
import com.nec.strudel.metrics.ProfilerValue;

@NotThreadSafe
public class CountingProfiler implements Profiler, CountInstrument {
    private final Map<String, Long> counts = new HashMap<String, Long>();
    private final String name;
    private final MeasurementState measure;

    public CountingProfiler(String name) {
        this.name = name;
        this.measure = MeasurementState.ALWAYS;
    }

    public CountingProfiler(String name, MeasurementState measure) {
        this.name = name;
        this.measure = measure;
    }

    @Override
    public void increment(String name) {
        add(name, 1);
    }

    @Override
    public void add(String name, long value) {
        if (measure.isMeasuring()) {
            Long val = counts.get(name);
            if (val != null) {
                val += value;
                counts.put(name, val);
            } else {
                counts.put(name, value);
            }
        }
    }

    @Override
    public JsonObject getValue() {
        return ProfilerValue.builder(name)
                .set(counts).build();
    }

    public static NoCount NO_COUNT = new NoCount();

    public static class NoCount implements Profiler, CountInstrument {
        private final JsonObject empty = Json.createObjectBuilder().build();

        @Override
        public void increment(String name) {
        }

        @Override
        public void add(String name, long value) {
        }

        @Override
        public JsonObject getValue() {
            return empty;
        }

    }
}
