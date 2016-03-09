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

package com.nec.strudel.workload.exec.batch;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.json.JsonObject;

import org.apache.log4j.Logger;

import com.nec.strudel.Closeable;
import com.nec.strudel.metrics.Report;
import com.nec.strudel.workload.exec.WorkExec;
import com.nec.strudel.workload.state.WorkState;
import com.nec.strudel.workload.util.TimeValue;

public class BatchExec extends WorkExec {
    private static final Logger LOGGER = Logger.getLogger(BatchExec.class);
    private final WorkState state;
    private final WorkThread[] workThreads;
    private final ExecutorService exec;
    private volatile boolean failed = false;

    public BatchExec(WorkState state, WorkThread[] workThreads,
            Closeable[] closeables) {
        super(workThreads.length, closeables);
        this.state = state;
        this.workThreads = workThreads;
        this.exec = Executors.newFixedThreadPool(workThreads.length);
    }

    public WorkThread[] getWorkThreads() {
        return workThreads;
    }

    @Override
    public String getState() {
        boolean allDone = true;
        for (WorkThread t : workThreads) {
            if (t.isDone()) {
                if (!t.isSuccessful()) {
                    fail();
                }
            } else {
                allDone = false;
            }
        }
        if (this.failed) {
            state.fail();
        } else if (allDone) {
            state.done();
        }
        return state.getState();
    }

    @Override
    public Report getReport() {
        Report[] reports = new Report[workThreads.length];
        for (int i = 0; i < workThreads.length; i++) {
            reports[i] = workThreads[i].getReport();
        }
        return Report.aggregate(reports);
    }

    @Override
    public void start(TimeValue slack) {
        if (state.start()) {
            int slackMs = (int) slack.toMillis();
            if (slackMs > 0) {
                Random rand = new Random();
                for (WorkThread w : workThreads) {
                    int wait = rand.nextInt(slackMs);
                    exec.execute(new StartSlackRunner(
                            TimeValue.milliseconds(wait), w));
                }
            } else {
                for (WorkThread w : workThreads) {
                    exec.execute(new StartSlackRunner(w));
                }
            }
            /**
             * no further tasks accepted:
             */
            exec.shutdown();
        }
    }

    @Override
    public void operate(String name, JsonObject data) {
        state.operate(name, data);
    }

    @Override
    public synchronized void stop() {
        if (state.stop()) {
            for (WorkThread w : workThreads) {
                w.stop();
            }
        }
    }

    @Override
    public synchronized boolean terminate(long timeout, TimeUnit unit)
            throws InterruptedException {
        if (state.isRunning()) {
            stop();
        }
        if (state.terminate()) {
            return exec.awaitTermination(timeout, unit);
        } else {
            return true;
        }
    }

    protected void fail() {
        failed = true;
    }

    public static WorkExec create(WorkState state, WorkThread[] workThreads,
            Closeable... closeables) {
        return new BatchExec(state, workThreads, closeables);
    }

    /**
     * for unit tests
     */
    public static WorkThread[] findWorkThreads(WorkExec workExec) {
        if (workExec instanceof BatchExec) {
            return ((BatchExec) workExec).getWorkThreads();
        }
        return null;
    }

    protected class StartSlackRunner implements Runnable {
        private final Runnable task;
        private final long waitMs;

        StartSlackRunner(TimeValue wait, Runnable task) {
            this.waitMs = wait.toMillis();
            this.task = task;
        }

        StartSlackRunner(Runnable task) {
            this.waitMs = 0;
            this.task = task;
        }

        @Override
        public void run() {
            if (waitMs > 0) {
                try {
                    Thread.sleep(waitMs);
                } catch (InterruptedException ex) {
                    return;
                }
            }
            try {
                task.run();
            } catch (Exception ex) {
                LOGGER.error("work thread failed", ex);
                fail();
            }
        }
    }
}