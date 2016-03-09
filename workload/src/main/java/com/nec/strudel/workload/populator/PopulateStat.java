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

package com.nec.strudel.workload.populator;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.instrument.OperationStat;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.management.resource.Getter;
import com.nec.strudel.management.resource.ManagedResource;
import com.nec.strudel.management.resource.ResourceName;

@ThreadSafe
@ManagedResource(description = "monitors the performance of populate tasks")
public class PopulateStat {
    public static final int WINDOW_SIZE = 5;
    public static final long WINDOW_STEP_MS = 1000;
    private final OperationStat mon;
    private final String name;
    private final int threadNum;

    public PopulateStat(String name, int threadNum, ProfilerService profs) {
        mon = profs.createOperationStat(WINDOW_SIZE, WINDOW_STEP_MS);
        this.name = name;
        this.threadNum = threadNum;
    }

    @ResourceName
    public String getName() {
        return name;
    }

    @Getter(description = "number of threads per worker")
    public int getThreadNum() {
        return threadNum;
    }

    @Getter(description = "number of populator execution units per second")
    public double getTasksPerSec() {
        return mon.getOperationsPerSec();
    }

    @Getter(description = "average time (ms) for one populator execution unit")
    public double getAvgTaskTime() {
        return mon.getAverageOperationTime();
    }

    public PopulateProfiler profiler() {
        return new PopulateProfiler(mon);
    }
}
