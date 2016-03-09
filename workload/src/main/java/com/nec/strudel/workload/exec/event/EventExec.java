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

package com.nec.strudel.workload.exec.event;

import java.util.concurrent.TimeUnit;

import javax.json.JsonObject;

import com.nec.strudel.Closeable;
import com.nec.strudel.metrics.Report;
import com.nec.strudel.workload.exec.WorkExec;
import com.nec.strudel.workload.state.WorkState;
import com.nec.strudel.workload.util.TimeValue;

/**
 * Executor to run a workload that emulates timed events
 *
 * @author tatemura
 *
 * @param <E>
 *            type of event result
 */
public class EventExec<E> extends WorkExec {
    public static <E> EventExec<E> create(EventController ctrl,
            EventSeq<E> events,
            int numOfThreads, WorkState state,
            Closeable... closeables) {
        return new EventExec<E>(ctrl, events, numOfThreads, state, closeables);
    }

    private final EventExecutor<E> executor;
    private final EventController ctrl;
    private final WorkState state;

    public EventExec(EventController ctrl, EventSeq<E> events,
            int numOfThreads, WorkState state,
            Closeable... closeables) {
        super(numOfThreads, closeables);
        this.executor = new EventExecutor<>(events, numOfThreads);
        this.ctrl = ctrl;
        this.state = state;
    }

    @Override
    public String getState() {
        if (executor.hasFailure()) {
            state.fail();
        }
        return state.getState();
    }

    @Override
    public Report getReport() {
        return ctrl.getReport();
    }

    @Override
    public void start(TimeValue slack) {
        if (state.start()) {
            ctrl.setStartSlack(slack);
            executor.start();
        }
    }

    @Override
    public void operate(String name, JsonObject data) {
        if (!ctrl.operate(name, data)) {
            state.operate(name, data);
        }
    }

    @Override
    public void stop() {
        if (state.stop()) {
            executor.stop();
        }
    }

    @Override
    public boolean terminate(long timeout, TimeUnit unit)
            throws InterruptedException {
        if (state.isRunning()) {
            stop();
        }
        if (state.terminate()) {
            return executor.awaitTermination(timeout, unit);
        } else {
            return true;
        }
    }

}