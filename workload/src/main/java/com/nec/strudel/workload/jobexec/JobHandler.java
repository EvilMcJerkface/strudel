package com.nec.strudel.workload.jobexec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.nec.congenio.ConfigDescription;
import com.nec.congenio.ConfigValue;
import com.nec.congenio.exec.ValueHandler;
import com.nec.strudel.exceptions.WorkloadException;
import com.nec.strudel.workload.env.Environment;
import com.nec.strudel.workload.env.EnvironmentConfig;
import com.nec.strudel.workload.job.Job;
import com.nec.strudel.workload.job.JobInfo;
import com.nec.strudel.workload.job.JobSuite;
import com.nec.strudel.workload.job.Task;
import com.nec.strudel.workload.util.TimeUtil;

/**
 * A handler class that runs a job or job suite. It will generate the following
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
public class JobHandler implements ValueHandler {
    private static final Logger LOGGER = Logger.getLogger(JobHandler.class);
    private static final long SLEEP_BETWEEN_JOBS = 1000;
    private final JobInfo info;
    private EnvironmentConfig lastConf;
    private TaskRunnerFactory runnerFactory =
            new TaskRunnerFactory();

    public JobHandler(JobInfo info) {
        this.info = info;
    }

    public void setTaskRunnerFactory(TaskRunnerFactory factory) {
        this.runnerFactory = factory;
    }

    @Override
    public void init(ConfigDescription cdl) throws Exception {
        info.setId(JobSuite.findId(cdl));
        String out = JobSuite.findOutput(cdl);
        if (out != null) {
            info.setOutDir(out);
            prepare(cdl);
        }
    }

    @Override
    public void value(int idx, ConfigValue value) throws Exception {
        final ConfigValue job;
        if (Job.TAG_NAME.equalsIgnoreCase(value.getName())) {
            job = value;
        } else {
            job = value.getValue(Job.TAG_NAME);
        }
        exec(new Job(info.copy(idx), job));
    }

    protected void exec(Job job) throws Exception {
        EnvironmentConfig envConf = job.createEnv();
        if (lastConf == null) {
            startJobSuite(envConf);
        } else {
            Thread.sleep(SLEEP_BETWEEN_JOBS);
        }
        lastConf = envConf;
        Environment env = envConf.create();
        env.start(envConf.getStart());
        boolean failed = false;
        try {
            runTasks(job);
        } catch (Exception ex) {
            failed = true;
            LOGGER.error(
                    "exception during task execution. stopping...", ex);
            throw ex;
        } finally {
            env.stop(envConf.getStop());
            if (failed) {
                LOGGER.error("job #" + job.getInfo().getJobId()
                        + " failed.");
            }
        }
        
    }

    @Override
    public void close() throws Exception {
        if (lastConf != null) {
            stopJobSuite(lastConf);
        }
    }
    
    protected void runTasks(Job job) {
        long startTime = System.currentTimeMillis();
        for (Task t : job.createTasks()) {
            Runnable runner = runnerFactory.create(t, job);
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

    protected void startJobSuite(EnvironmentConfig envConf) {
        Environment env = envConf.create();
        env.startSuite(envConf.getStartSuite());
    }

    protected void stopJobSuite(EnvironmentConfig envConf) {
        Environment env = envConf.create();
        env.stopSuite(envConf.getStopSuite());
    }

    protected void prepare(ConfigDescription cdl) {
        try {
            File outDir = prepareOutDir();
            addLog4jAppender(outDir);
            FileWriter writer = new FileWriter(saveFile(outDir));
            cdl.write(writer);
            writer.close();
        } catch (IOException ex) {
            throw new WorkloadException(
                    "failed to save job suite info", ex);
        }
    }

    private File prepareOutDir() {
        File outDir = new File(info.getOutDir());
        outDir.mkdirs();
        return outDir;
    }

    private File saveFile(File outDir) {
        return new File(outDir, "jobsuite-" + info.getId() + ".xml");
    }

    private void addLog4jAppender(File outDir)
            throws IOException {
        File file = new File(outDir, "joblog-" + info.getId() + ".log");
        BasicConfigurator.configure(new FileAppender(
                new PatternLayout("[%d %c{3}] %p %m %n"),
                file.getAbsolutePath()));
    }

}
