package com.nec.strudel.workload.jobexec;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.nec.strudel.workload.job.Job;
import com.nec.strudel.workload.job.JobInfo;
import com.nec.strudel.workload.job.Task;
import com.nec.strudel.workload.job.WorkloadTask;
import com.nec.strudel.workload.test.Resources;

public class JobHandlerTest {

    @Test
    public void test() throws Exception {
        File jobsuite = Resources.getFile("jobsuite002");
        JobInfo info = new JobInfo();
        info.setPath(jobsuite.getAbsolutePath());
        JobHandler handler = new JobHandler(info);
        handler.setTaskRunnerFactory(new TestRunerFactory());
        JobCli cli = new JobCli("test");
        cli.execute(handler,
                jobsuite.getAbsolutePath());
    }

    static class TestRunerFactory extends TaskRunnerFactory {
        private int idx;
        private int[] threads = {1,2,3,4};

        @Override
        public Runnable create(final Task task, final Job job) {
            return new Runnable() {
                @Override
                public void run() {
                    validate(task, job);
                }
            };
        }

        public void validate(Task task, Job job) {
            try {
                WorkloadTask wk = (WorkloadTask) task;
                int th = threads[idx];
                assertEquals(th, wk.numOfThreads());
            } finally {
                idx++;
            }
        }
    }
}
