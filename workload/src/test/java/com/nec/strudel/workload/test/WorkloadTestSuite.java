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
