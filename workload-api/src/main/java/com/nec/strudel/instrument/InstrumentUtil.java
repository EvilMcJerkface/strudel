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

package com.nec.strudel.instrument;

import javax.json.Json;
import javax.json.JsonObject;

public final class InstrumentUtil {

    private InstrumentUtil() {
    }

    public static <T> Instrumented<T> profiled(T obj, Profiler prof) {
        return new ProfiledObject<T>(obj, prof);
    }

    public static <T> Instrumented<T> uninstrumented(final T obj) {
        return new Instrumented<T>() {

            @Override
            public T getObject() {
                return obj;
            }

            @Override
            public Profiler getProfiler() {
                return NO_PROF;
            }
        };
    }

    private static final Profiler NO_PROF = new Profiler() {
        private final JsonObject empty = Json.createObjectBuilder().build();

        @Override
        public JsonObject getValue() {
            return empty;
        }

    };

    static class ProfiledObject<T> implements Instrumented<T> {
        private final T con;
        private final Profiler prof;

        public ProfiledObject(T con,
                Profiler prof) {
            this.con = con;
            this.prof = prof;
        }

        @Override
        public T getObject() {
            return con;
        }

        @Override
        public Profiler getProfiler() {
            return prof;
        }

    }
}
