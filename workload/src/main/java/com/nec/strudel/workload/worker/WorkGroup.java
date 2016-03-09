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

package com.nec.strudel.workload.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import javax.json.JsonObject;

import org.apache.log4j.Logger;

import com.nec.strudel.exceptions.WorkloadException;
import com.nec.strudel.metrics.Report;
import com.nec.strudel.workload.com.Caller;
import com.nec.strudel.workload.job.WorkRequest;
import com.nec.strudel.workload.server.WorkerClient;
import com.nec.strudel.workload.util.TimeUtil;

public class WorkGroup implements Caller {
    private static final AtomicLong LOCAL_ID_COUNTER = new AtomicLong();

    protected static String newWorkId() {
        return "local." + Long.toHexString(
                LOCAL_ID_COUNTER.getAndIncrement());
    }

    public static WorkGroup create(WorkRequest... workRequests) {
        ExecutorService exec = Executors.newCachedThreadPool();
        List<Future<InitResult>> res = new ArrayList<Future<InitResult>>();
        for (WorkRequest w : workRequests) {
            res.add(exec.submit(new Initialize(w)));
        }
        Worker[] workers = new Worker[workRequests.length];
        for (int i = 0; i < workRequests.length; i++) {
            Future<InitResult> resultFuture = res.get(i);
            try {
                InitResult initRes = resultFuture.get();
                workers[i] = initRes.getWorker();
            } catch (InterruptedException ex) {
                LOGGER.error("initialization interrupted", ex);
                throw new WorkloadException(
                        "initialization interrupted", ex);
            } catch (ExecutionException ex) {
                LOGGER.error("failed to initialize", ex);
                throw new WorkloadException(
                        "failed to initialize", ex);
            }
        }
        return new WorkGroup(workers, exec);
    }

    private static final Logger LOGGER = Logger.getLogger(WorkGroup.class);
    private final Worker[] workers;
    private final ExecutorService exec;

    public WorkGroup(Worker[] works, ExecutorService exec) {
        this.workers = works;
        this.exec = exec;
    }

    public Worker[] getWorkers() {
        return workers;
    }

    public int size() {
        return workers.length;
    }

    public void close() {
        exec.shutdown();
    }

    public <T> List<Future<T>> call(List<? extends Callable<T>> calls) {
        List<Future<T>> res = new ArrayList<Future<T>>();
        for (Callable<T> c : calls) {
            res.add(exec.submit(c));
        }
        return res;
    }

    public void operate(String name, JsonObject arg)
            throws InterruptedException {
        List<Future<CommandResult>> res = new ArrayList<Future<CommandResult>>();
        for (Worker w : workers) {
            res.add(exec.submit(
                    new Operate(w, name, arg)));
        }
        waitResults(res);
    }

    public void start()
            throws InterruptedException {
        List<Future<CommandResult>> res = new ArrayList<Future<CommandResult>>();
        for (Worker w : workers) {
            res.add(exec.submit(new Start(w)));
        }
        waitResults(res);
    }

    public void stop()
            throws InterruptedException {
        List<Future<CommandResult>> res = new ArrayList<Future<CommandResult>>();
        for (Worker w : workers) {
            res.add(exec.submit(new Stop(w)));
        }
        waitResults(res);
    }

    public void terminate()
            throws InterruptedException {
        List<Future<CommandResult>> res = new ArrayList<Future<CommandResult>>();
        for (Worker w : workers) {
            res.add(exec.submit(new Terminate(w)));
        }
        waitResults(res);
    }

    public Report getReport()
            throws InterruptedException {
        List<Future<Report>> res = new ArrayList<Future<Report>>();
        for (Worker w : workers) {
            res.add(exec.submit(
                    new GetReport(w)));
        }
        Report[] reports = new Report[workers.length];
        for (int i = 0; i < reports.length; i++) {
            try {
                reports[i] = res.get(i).get();
            } catch (ExecutionException ex) {
                LOGGER.error("failed to get report", ex);
                throw new WorkloadException(
                        "failed to get a report", ex);
            }
        }
        return Report.aggregate(reports);
    }

    public List<String> getStates()
            throws InterruptedException {
        List<Future<String>> res = new ArrayList<Future<String>>();
        for (Worker w : workers) {
            res.add(exec.submit(new GetState(w)));
        }
        List<String> states = new ArrayList<String>(res.size());
        for (Future<String> s : res) {
            try {
                states.add(s.get());
            } catch (ExecutionException ex) {
                LOGGER.error("failed to get state", ex);
                throw new WorkloadException(
                        "failed to get a state", ex);
            }
        }
        return states;
    }

    public String[] getStates(boolean[] mask) throws InterruptedException {
        String[] states = new String[workers.length];
        @SuppressWarnings("unchecked")
        Future<String>[] futures = new Future[workers.length];
        for (int i = 0; i < workers.length; i++) {
            if (mask[i]) {
                futures[i] = exec.submit(
                        new GetState(workers[i]));
            }
        }
        for (int i = 0; i < workers.length; i++) {
            try {
                states[i] = futures[i].get();
            } catch (ExecutionException ex) {
                LOGGER.error("failed to get state", ex);
                throw new WorkloadException(
                        "failed to get a state", ex);
            }
        }
        return states;
    }

    private static final int SKEW_RATIO = 2;
    private static final int SLOW_TIME = 10000;

    private void waitResults(List<Future<CommandResult>> res)
            throws InterruptedException {
        long maxTime = 0;
        long timeSum = 0;
        String id = "";
        int idx = 0;
        for (Future<CommandResult> r : res) {
            try {
                CommandResult cr = r.get();
                long time = cr.getTime();
                timeSum += time;
                if (time > maxTime) {
                    maxTime = time;
                    id = cr.getId();
                }
                idx++;
            } catch (ExecutionException ex) {
                String workId = workers[idx].getWorkId();
                LOGGER.error(
                        "failed to execute a command at "
                                + workId,
                        ex);
                throw new WorkloadException(
                        "failed to execute a command at "
                                + workId,
                        ex);
            }
        }
        int count = res.size();
        if (count > 0) {
            long avg = timeSum / count;
            if (avg > 0 && maxTime > SLOW_TIME
                    && maxTime / avg > SKEW_RATIO) {
                LOGGER.warn("slow command:"
                        + id + " ("
                        + TimeUtil.formatTimeMs(maxTime)
                        + " avg = "
                        + TimeUtil.formatTimeMs(avg));
            } else {
                LOGGER.debug("slow command:"
                        + id + " ("
                        + TimeUtil.formatTimeMs(maxTime)
                        + " avg = "
                        + TimeUtil.formatTimeMs(avg));
            }
        }
    }

    static class GetReport implements Callable<Report> {
        private final Worker worker;

        public GetReport(Worker worker) {
            this.worker = worker;
        }

        @Override
        public Report call() throws Exception {
            return worker.getReport();
        }
    }

    static class CommandResult {
        private final String id;
        private final long time;

        CommandResult(String id, long time) {
            this.id = id;
            this.time = time;
        }

        public long getTime() {
            return time;
        }

        public String getId() {
            return id;
        }
    }

    static class InitResult extends CommandResult {
        private final Worker worker;

        InitResult(String id, Worker worker, long time) {
            super(id, time);
            this.worker = worker;
        }

        public Worker getWorker() {
            return worker;
        }
    }

    static class Initialize implements Callable<InitResult> {
        private final WorkRequest work;

        public Initialize(WorkRequest workRequest) {
            this.work = workRequest;
        }

        @Override
        public InitResult call() {
            long start = System.currentTimeMillis();
            Worker worker = createWorker();
            long time = System.currentTimeMillis() - start;
            return new InitResult(worker.getWorkId(),
                    worker, time);
        }

        private Worker createWorker() {
            if (work.getNode().isLocal()) {
                return LocalWorker.create(newWorkId(), work);
            } else {
                WorkerClient client = new WorkerClient();
                return client.create(work);
            }
        }

    }

    static class Operate implements Callable<CommandResult> {
        private final Worker worker;
        private final String name;
        private final JsonObject arg;

        Operate(Worker worker, String name, JsonObject arg) {
            this.worker = worker;
            this.name = name;
            this.arg = arg;
        }

        @Override
        public CommandResult call() throws Exception {
            long start = System.currentTimeMillis();
            worker.operate(name, arg);
            long time = System.currentTimeMillis() - start;
            return new CommandResult(worker.getWorkId(),
                    time);
        }
    }

    static class Start implements Callable<CommandResult> {
        private final Worker worker;

        Start(Worker worker) {
            this.worker = worker;
        }

        @Override
        public CommandResult call() throws Exception {
            long start = System.currentTimeMillis();
            worker.start();
            long time = System.currentTimeMillis() - start;
            return new CommandResult(worker.getWorkId(),
                    time);
        }
    }

    static class Stop implements Callable<CommandResult> {
        private final Worker worker;

        Stop(Worker worker) {
            this.worker = worker;
        }

        @Override
        public CommandResult call() throws Exception {
            long start = System.currentTimeMillis();
            worker.stop();
            long time = System.currentTimeMillis() - start;
            return new CommandResult(worker.getWorkId(),
                    time);
        }
    }

    static class Terminate implements Callable<CommandResult> {
        private final Worker worker;

        Terminate(Worker worker) {
            this.worker = worker;
        }

        @Override
        public CommandResult call() throws Exception {
            long start = System.currentTimeMillis();
            worker.terminate();
            long time = System.currentTimeMillis() - start;
            return new CommandResult(worker.getWorkId(),
                    time);
        }
    }

    static class GetState implements Callable<String> {
        private final Worker worker;

        GetState(Worker worker) {
            this.worker = worker;
        }

        @Override
        public String call() throws Exception {
            return worker.getState();
        }
    }
}