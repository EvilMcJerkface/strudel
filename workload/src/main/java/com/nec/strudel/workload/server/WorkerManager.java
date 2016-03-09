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

package com.nec.strudel.workload.server;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.json.JsonObject;

import org.apache.log4j.Logger;

import com.nec.strudel.metrics.Report;
import com.nec.strudel.workload.job.WorkRequest;
import com.nec.strudel.workload.worker.LocalWorker;
import com.nec.strudel.workload.worker.Worker;

public class WorkerManager implements WorkerService {
    private static final Logger LOGGER = Logger.getLogger(WorkerManager.class);
    private final AtomicLong idCounter = new AtomicLong();
    private final ConcurrentMap<String, Worker> workers = new ConcurrentHashMap<String, Worker>();

    @Override
    public WorkStatus init(WorkRequest work) {
        String id = Long.toHexString(idCounter.getAndIncrement());
        LOGGER.info("creating work: id=" + id);
        try {
            Worker worker = LocalWorker.create(id, work);
            workers.put(id, worker);
            return WorkStatus.stat(id, worker.getState());
        } catch (Throwable ex) {
            LOGGER.error("init failed (id=" + id + ")", ex);
            return WorkStatus.error(
                    "exception during init:" + ex.getClass()
                            + ":" + ex.getMessage());
        }
    }

    @Override
    public WorkStatus start(String workId) {
        Worker worker = workers.get(workId);
        if (worker != null) {
            try {
                worker.start();
                LOGGER.info("start: " + workId);
                return WorkStatus.stat(workId, worker.getState());
            } catch (Throwable ex) {
                LOGGER.error("worker start failed:id="
                        + worker.getWorkId()
                        + " state=" + worker.getState(), ex);
                return WorkStatus.error(workId, ex.getMessage());
            }
        } else {
            LOGGER.warn("unknown work to start: " + workId);
            return WorkStatus.unknown(workId);
        }
    }

    @Override
    public WorkStatus operate(String workId, String name,
            JsonObject arg) {
        Worker worker = workers.get(workId);
        if (worker != null) {
            try {
                worker.operate(name, arg);
                return WorkStatus.stat(workId, worker.getState());
            } catch (Throwable ex) {
                LOGGER.error("worker operate failed:id="
                        + worker.getWorkId()
                        + " state=" + worker.getState()
                        + " op=" + name, ex);
                return WorkStatus.error(workId, ex.getMessage());
            }
        } else {
            LOGGER.warn("unknown work to operate "
                    + name + ": " + workId);
            return WorkStatus.unknown(workId);
        }
    }

    @Override
    public WorkStatus stop(String workId) {
        Worker worker = workers.get(workId);
        if (worker != null) {
            worker.stop();
        } else {
            LOGGER.warn("unknown work to stop: " + workId);
        }
        return WorkStatus.stat(workId, worker.getState());
    }

    @Override
    public WorkStatus getStatus(String workId) {
        Worker worker = workers.get(workId);
        if (worker != null) {
            return WorkStatus.stat(workId, worker.getState());
        } else {
            return WorkStatus.unknown(workId);
        }
    }

    @Override
    public JsonObject getReport(String workId) {
        Worker worker = workers.get(workId);
        if (worker != null) {
            return worker.getReport().toJson();
        } else {
            LOGGER.warn("unknown work to report: " + workId);
            return Report.none().toJson();
        }
    }

    @Override
    public WorkStatus terminate(String workId) throws InterruptedException {
        Worker worker = workers.get(workId);
        if (worker != null) {
            try {
                worker.terminate();
            } catch (Throwable ex) {
                LOGGER.error("worker terminate failed:id="
                        + worker.getWorkId()
                        + " state=" + worker.getState(), ex);
                return WorkStatus.error(workId, ex.getMessage());
            } finally {
                workers.remove(workId);
            }

        } else {
            LOGGER.warn("unknown work to terminate: " + workId);
        }
        return WorkStatus.stat(workId, worker.getState());
    }

    @Override
    public Set<String> works() {
        return Collections.unmodifiableSet(workers.keySet());
    }
}
