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

package com.nec.strudel.session;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Result {
    private final boolean success;
    private final Map<String, Object> values;
    private final List<Warn> warns;
    private final String mode;

    public Result(boolean success,
            Map<String, Object> values, List<Warn> warns) {
        this(success, "", values, warns);
    }

    public Result(boolean success, String mode,
            Map<String, Object> values, List<Warn> warns) {
        this.success = success;
        this.mode = mode;
        this.values = Collections.unmodifiableMap(values);
        this.warns = Collections.unmodifiableList(warns);
    }

    public boolean isSuccess() {
        return success;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ParamName paramName) {
        return (T) values.get(paramName.name());
    }

    /**
     * @return an empty string if no mode is defined.
     */
    public String getMode() {
        return mode;
    }

    public boolean hasMode() {
        return !mode.isEmpty();
    }

    public boolean hasWarning() {
        return !warns.isEmpty();
    }

    public List<Warn> getWarnings() {
        return warns;
    }

    public static class Warn {
        private final String msg;

        public Warn(String msg) {
            this.msg = msg;
        }

        public String getMessage() {
            return msg;
        }
    }
}
