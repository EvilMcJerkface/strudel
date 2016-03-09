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

import javax.json.JsonObject;

import com.nec.strudel.metrics.Report;
import com.nec.strudel.workload.cluster.Node;
import com.nec.strudel.workload.job.WorkRequest;
import com.nec.strudel.workload.server.rest.client.RestWorkerServiceProvider;
import com.nec.strudel.workload.worker.Worker;

public class WorkerClient {
    private final WorkerServiceProvider sp;

    public WorkerClient(WorkerServiceProvider sp) {
        this.sp = sp;
    }

    public WorkerClient() {
        this(new RestWorkerServiceProvider());
    }

    public Worker create(WorkRequest work) {
        Node node = work.getNode();
        WorkerService service = sp.create(node);
        WorkStatus stat = service.init(work);
        return new RemoteWorker(node.getUrl(),
                stat.getWorkId(), service);
    }

    protected static class RemoteWorker implements Worker {
        private final String url;
        private final String workId;
        private final WorkerService service;

        protected RemoteWorker(String url,
                String id, WorkerService service) {
            this.url = url;
            this.workId = id;
            this.service = service;
        }

        @Override
        public String getWorkId() {
            return url + "#" + workId;
        }

        @Override
        public String getState() {
            WorkStatus stat = service.getStatus(workId);
            return stat.getState();
        }

        @Override
        public void start() {
            service.start(workId);
        }

        @Override
        public void operate(String name, JsonObject arg) {
            service.operate(workId, name, arg);
        }

        @Override
        public void stop() {
            service.stop(workId);
        }

        @Override
        public void terminate() throws InterruptedException {
            service.terminate(workId);
        }

        @Override
        public Report getReport() {
            return Report.toReport(service.getReport(workId));
        }
    }
}
