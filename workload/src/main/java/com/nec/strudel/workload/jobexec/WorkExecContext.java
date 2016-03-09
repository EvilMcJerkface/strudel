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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.json.JsonObject;

import org.apache.log4j.Logger;

import com.nec.strudel.json.JsonValues;
import com.nec.strudel.workload.com.CommandContext;
import com.nec.strudel.workload.worker.WorkGroup;

public class WorkExecContext implements CommandContext {
    private WorkGroup wg;
    private final Logger logger;
    private List<WorkloadResult> results = new ArrayList<WorkloadResult>();

    public WorkExecContext(WorkGroup wg, Logger logger) {
        this.wg = wg;
        this.logger = logger;
    }

    @Override
    public <T> List<Future<T>> call(List<? extends Callable<T>> calls) {
        return wg.call(calls);
    }

    @Override
    public Logger logger() {
        return logger;
    }

    public WorkGroup workGroup() {
        return wg;
    }

    public synchronized void result(WorkloadResult res) {
        this.results.add(res);
    }

    public synchronized WorkloadResult getResult() {
        List<String> warns = new ArrayList<String>();
        List<JsonObject> values = new ArrayList<JsonObject>();
        for (WorkloadResult r : results) {
            values.add(r.getResult());
            warns.addAll(r.getWarns());
        }
        return new WorkloadResult(
                JsonValues.union(values), warns);
    }

    public void terminate() throws InterruptedException {
        wg.terminate();
    }

    public void close() {
        wg.close();
    }

}
