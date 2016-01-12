package com.nec.strudel.workload.test;

import com.nec.strudel.workload.com.ProcessCommandConfig;
import com.nec.strudel.workload.job.Job;

public class ResourceNames {

	public static final ResourceFile<Job> JOB1 = Resources.of("job001", Job.class);

	public static final String JOB_SUITE1 = "jobsuite001";

	public static final ResourceFile<ProcessCommandConfig> COMMAND001 =
			Resources.of("command001", ProcessCommandConfig.class);
}
