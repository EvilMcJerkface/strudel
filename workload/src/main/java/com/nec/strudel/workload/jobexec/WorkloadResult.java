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

package com.nec.strudel.workload.jobexec;

import java.util.Collection;
import java.util.Collections;

import javax.json.JsonObject;

public class WorkloadResult {

    private final Collection<String> warns;
    private final JsonObject result;

    public WorkloadResult(JsonObject result,
            Collection<String> warns) {
        this.warns = warns;
        this.result = result;
    }

    public WorkloadResult(JsonObject result) {
        this.warns = Collections.emptyList();
        this.result = result;
    }

    public JsonObject getResult() {
        return result;
    }

    public Collection<String> getWarns() {
        return warns;
    }

}