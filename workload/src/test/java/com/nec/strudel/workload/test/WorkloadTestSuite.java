package com.nec.strudel.workload.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.nec.strudel.workload.cluster.test.ClusterTest;
import com.nec.strudel.workload.job.test.JobSuiteTest;
import com.nec.strudel.workload.job.test.JobTest;
import com.nec.strudel.workload.job.test.PopulateTaskTest;
import com.nec.strudel.workload.job.test.WorkConfigTest;
import com.nec.strudel.workload.job.test.WorkloadTaskTest;
import com.nec.strudel.workload.jobexec.test.PopulateRunnerTest;
import com.nec.strudel.workload.param.test.ParamSequenceTest;
import com.nec.strudel.workload.session.test.SessionConfigTest;
import com.nec.strudel.workload.target.test.DatabaseConfigTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	WorkConfigTest.class,
	ClusterTest.class,
	DatabaseConfigTest.class,
	WorkloadTaskTest.class,
	JobTest.class,
	JobSuiteTest.class,
	ParamSequenceTest.class,
	PopulateRunnerTest.class,
	PopulateTaskTest.class,
	SessionConfigTest.class
})
public class WorkloadTestSuite {

}
