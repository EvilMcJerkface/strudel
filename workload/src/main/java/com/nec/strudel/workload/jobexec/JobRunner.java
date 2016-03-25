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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.nec.congenio.ConfigDescription;
import com.nec.strudel.exceptions.WorkloadException;
import com.nec.strudel.workload.env.Environment;
import com.nec.strudel.workload.env.EnvironmentConfig;
import com.nec.strudel.workload.job.Job;
import com.nec.strudel.workload.job.JobInfo;
import com.nec.strudel.workload.job.JobSuite;
import com.nec.strudel.workload.job.Task;
import com.nec.strudel.workload.util.TimeUtil;

/**
 * A runnable class that runs a job or job suite. It will generate the following
 * data when it executes a job suite:
 * <ul>
 * <li>${output}/jobsuite-${id}.xml - a configuration file after resolving
 * inheritance
 * <li>${output}/joblog-${id}.log - a Log4j log file during job execution
 * <li>${output}/data/*.json - results of individual jobs in the job suite.
 * </ul>
 * 
 * @author tatemura
 *
 */
public abstract class JobRunner implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(JobRunner.class);
    private static final long SLEEP_BETWEEN_JOBS = 1000;
    private Exception exception;

    public static JobRunner create(File file) {
        String path = file.getAbsolutePath();
        JobInfo info = new JobInfo(path);
        ConfigDescription cxml = ConfigDescription.create(file,
                JobSuite.baseDescription());
        if (JobSuite.TAG_NAME.equalsIgnoreCase(cxml.getName())) {
            return new JobSuiteRunner(
                    JobSuite.create(info, cxml));
        } else {
            return new SingleJobRunner(
                    new Job(info, cxml.resolve()));
        }
    }

    static class SingleJobRunner extends JobRunner {
        private final Job job;

        public SingleJobRunner(Job job) {
            this.job = job;
        }

        protected Iterable<Job> jobs() {
            return Arrays.asList(job);
        }

        protected void prepare() {
            // do nothing
        }
    }

    static class JobSuiteRunner extends JobRunner {
        private final JobSuite jobSuite;

        public JobSuiteRunner(JobSuite jobSuite) {
            this.jobSuite = jobSuite;
        }

        protected Iterable<Job> jobs() {
            return jobSuite;
        }

        protected void prepare() {
            String id = jobSuite.getId();
            try {
                File outDir = prepareOutDir();
                recordJobSuiteInfo(outDir, id);
                addLog4jAppender(outDir, id);
            } catch (IOException ex) {
                throw new WorkloadException(
                        "failed to save job suite info", ex);
            }
        }

        private File prepareOutDir() {
            File outDir = new File(jobSuite.getOutput());
            outDir.mkdirs();
            return outDir;
        }

        private void recordJobSuiteInfo(File outDir, String id)
                throws IOException {
            File file = new File(outDir, "jobsuite-" + id + ".xml");
            FileWriter writer = new FileWriter(file);
            jobSuite.write(writer);
            writer.close();

        }

        private void addLog4jAppender(File outDir, String id)
                throws IOException {
            File file = new File(outDir, "joblog-" + id + ".log");
            BasicConfigurator.configure(new FileAppender(
                    new PatternLayout("[%d %c{3}] %p %m %n"),
                    file.getAbsolutePath()));
        }

    }

    public JobRunner() {
    }

    @Override
    public void run() {
        prepare();
        int num = 0;
        EnvironmentConfig lastConf = null;
        try {
            for (Job job : jobs()) {
                EnvironmentConfig envConf = job.createEnv();
                if (num == 0) {
                    startJobSuite(envConf);
                } else {
                    try {
                        Thread.sleep(SLEEP_BETWEEN_JOBS);
                    } catch (InterruptedException ex) {
                        LOGGER.error("interrupted", ex);
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                lastConf = envConf;
                num++;
                boolean success = runJob(job, envConf);
                if (!success) {
                    LOGGER.error("job #" + num
                            + " failed.");
                    break;
                }
            }
        } finally {
            if (lastConf != null) {
                stopJobSuite(lastConf);
            }
        }
    }

    protected abstract void prepare();

    protected abstract Iterable<Job> jobs();

    protected void startJobSuite(EnvironmentConfig envConf) {
        Environment env = envConf.create();
        env.startSuite(envConf.getStartSuite());
    }

    protected void stopJobSuite(EnvironmentConfig envConf) {
        Environment env = envConf.create();
        env.stopSuite(envConf.getStopSuite());
    }

    protected boolean runJob(Job job, EnvironmentConfig envConf) {
        Environment env = envConf.create();
        env.start(envConf.getStart());
        try {
            runTasks(job);
            return true;
        } catch (Exception ex) {
            exception = ex;
            LOGGER.error("exception during task execution. stopping...", ex);
            return false;
        } finally {
            env.stop(envConf.getStop());
        }
    }

    protected void runTasks(Job job) {
        long startTime = System.currentTimeMillis();
        for (Task t : job.createTasks()) {
            Runnable runner = new TaskRunnerFactory().create(t, job);
            long time = System.currentTimeMillis();
            LOGGER.info("start " + t.description());
            runner.run();
            LOGGER.info("done " + t.description()
                    + ": "
                    + TimeUtil.formatTimeMs(
                            System.currentTimeMillis() - time));
        }
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info("job done: total "
                + TimeUtil.formatTimeMs(duration));
    }

    public boolean hasError() {
        return exception != null;
    }

    public Exception getException() {
        return exception;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("args: xmlfile");
            return;
        }
        JobRunner jr = JobRunner.create(new File(args[0]));
        jr.run();
        if (jr.hasError()) {
            LOGGER.error("Job was not successful: "
                    + jr.getException().getMessage());
        }
    }
}
