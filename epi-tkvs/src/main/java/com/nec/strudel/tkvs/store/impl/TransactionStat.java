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

package com.nec.strudel.tkvs.store.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.instrument.GetOperationListener;
import com.nec.strudel.instrument.OperationStat;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.management.resource.Getter;
import com.nec.strudel.management.resource.ManagedResource;
import com.nec.strudel.management.resource.ResourceName;

@ThreadSafe
@ManagedResource(description = "monitors the performance of transaction")
public class TransactionStat {
    public static final int WINDOW_SIZE = 5;
    public static final long WINDOW_STEP_MS = TimeUnit.SECONDS.toMillis(1);

    public static TransactionStat create(String name, ProfilerService profs) {
        return new TransactionStat(name, profs, WINDOW_SIZE, WINDOW_STEP_MS);
    }

    public static TransactionStat create(String name, ProfilerService profs,
            int windowSize, long windowStepMs) {
        return new TransactionStat(name, profs, windowSize, windowStepMs);
    }

    private final String name;
    private final OperationStat commitMon;
    private final OperationStat getMon;

    public TransactionStat(String name, ProfilerService profs, int size,
            long step) {
        this.name = name;
        commitMon = profs.createOperationStat(size, step);
        getMon = profs.createOperationStat(size, step);
    }

    @GetOperationListener("commit")
    public OperationStat commitMonitor() {
        return commitMon;
    }

    @GetOperationListener("get")
    public OperationStat getMonitor() {
        return getMon;
    }

    @ResourceName
    public String getName() {
        return name;
    }

    @Getter(description = "average response time of a commit operation (ms)")
    public double getAvgCommitTime() {
        return commitMon.getAverageOperationTime();
    }

    @Getter(description = "number of commit operations per second")
    public double getCommitPerSec() {
        return commitMon.getOperationsPerSec();
    }

    @Getter(description = "average response time of a get operation (ms)")
    public double getAvgGetTime() {
        return getMon.getAverageOperationTime();
    }

    @Getter(description = "number of get operations per second")
    public double getGetPerSec() {
        return getMon.getOperationsPerSec();
    }
}
