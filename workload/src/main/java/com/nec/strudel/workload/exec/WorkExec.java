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

package com.nec.strudel.workload.exec;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.json.JsonObject;

import com.nec.strudel.Closeable;
import com.nec.strudel.metrics.Report;
import com.nec.strudel.workload.util.TimeValue;

public abstract class WorkExec implements Closeable {
    private final int numOfThreads;
    private Closeable[] closeables;

    public WorkExec(int numOfThreads,
            Closeable... closeables) {
        this.numOfThreads = numOfThreads;
        this.closeables = closeables;
    }

    public int numOfThreads() {
        return numOfThreads;
    }

    @Override
    public synchronized void close() {
        for (Closeable c : closeables) {
            c.close();
        }
    }

    public synchronized void addCloseable(Closeable... cs) {
        Closeable[] newcs = Arrays.copyOf(closeables,
                closeables.length + cs.length);
        for (int i = 0; i < cs.length; i++) {
            newcs[i + closeables.length] = cs[i];
        }
        this.closeables = newcs;
    }

    public abstract String getState();

    public abstract Report getReport();

    public abstract void start(TimeValue slack);

    public abstract void operate(String name, JsonObject data);

    public abstract void stop();

    public abstract boolean terminate(long timeout, TimeUnit unit)
            throws InterruptedException;

}
