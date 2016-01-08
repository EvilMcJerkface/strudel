package com.nec.strudel.workload.jobexec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

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

public abstract class JobRunner implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(JobRunner.class);
	private static final long SLEEP_BETWEEN_JOBS = 1000;
	private Exception exception;
	public static JobRunner create(File file) {
		String path = file.getAbsolutePath();
		JobInfo info = new JobInfo(path);
		ConfigDescription cxml = ConfigDescription.create(file);
		if (JobSuite.TAG_NAME.equalsIgnoreCase(cxml.getName())) {
			return new JobSuiteRunner(
					new JobSuite(info, cxml));
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
			String date = dateString();
			try {
				File outDir = prepareOutDir();
				recordJobSuiteInfo(outDir, date);
				addLog4jAppender(outDir, date);
			} catch (IOException e) {
				throw new WorkloadException(
					"failed to save job suite info", e);
			}
		}
		private String dateString() {
	    	SimpleDateFormat df =
	    			new SimpleDateFormat("yyyy-MM-dd'T'HHmmss");
	    	return df.format(new Date());
		}
		private File prepareOutDir() {
			File outDir = new File(jobSuite.getOutput());
			outDir.mkdirs();
			return outDir;
		}
		private void recordJobSuiteInfo(File outDir, String date)
				throws IOException {
	    	File file = new File(outDir, "jobsuite-"
	    				+ date + ".xml");
	    	jobSuite.info().setSavedPath(file.getAbsolutePath());
	    	FileWriter w = new FileWriter(file);
	    	jobSuite.write(w);
	    	w.close();

		}
		private void addLog4jAppender(File outDir, String date)
				throws IOException {
	    	File file = new File(outDir, "joblog-"
					+ date + ".log");
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
		for (Job job : jobs()) {
			if (num > 0) {
				try {
					Thread.sleep(SLEEP_BETWEEN_JOBS);
				} catch (InterruptedException e) {
					LOGGER.error("interrupted", e);
					Thread.currentThread().interrupt();
					break;
				}
			}
			num++;
			boolean success = runJob(job);
			if (!success) {
				LOGGER.error("job #" + num
						+ " failed.");
				break;
			}
		}
	}
	protected abstract void prepare();
	protected abstract Iterable<Job> jobs();

	protected boolean runJob(Job job) {
	    EnvironmentConfig envConf = job.createEnv();
	    Environment env = envConf.create();
	    env.start(envConf.getStart());
	    try {
	        runTasks(job);
	        return true;
	    } catch (Exception e) {
	    	exception = e;
	    	LOGGER.error("exception during task execution. stopping...", e);
	    	return false;
	    } finally {
	        env.stop(envConf.getStop());
	    }
	}
	protected void runTasks(Job job) {
        long startTime = System.currentTimeMillis();
        for (Task t : job.createTasks()) {
            Runnable r = TaskRunnerFactory.create(t, job);
            long time = System.currentTimeMillis();
            LOGGER.info("start " + t.description());
            r.run();
            LOGGER.info("done " + t.description()
                    + ": "
                    + TimeUtil.formatTimeMS(
                           System.currentTimeMillis() - time));
       }
        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info("job done: total "
                + TimeUtil.formatTimeMS(duration));
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
		JobRunner jr =
		JobRunner.create(new File(args[0]));
		jr.run();
		if (jr.hasError()) {
			LOGGER.error("Job was not successful: "
					+ jr.getException().getMessage());
		}
	}
}
