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
package com.nec.strudel.workload.job.test;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import javax.json.JsonObject;

import org.junit.Test;

import com.nec.strudel.target.impl.DatabaseConfig;
import com.nec.strudel.workload.job.Job;
import com.nec.strudel.workload.job.PopulateTask;
import com.nec.strudel.workload.job.Task;
import com.nec.strudel.workload.job.WorkloadTask;
import com.nec.strudel.workload.jobexec.PopulateRunner;
import com.nec.strudel.workload.out.OutputConfig;
import com.nec.strudel.workload.test.ResourceNames;
import com.nec.strudel.workload.test.Resources;

public class JobTest {
	@Test
	public void testJob() throws URISyntaxException {
	    File file = Resources.file(ResourceNames.JOB1);
		Job job = Job.create(file);
		List<Task> tasks = job.createTasks();
		assertEquals(2, tasks.size());
		assertTrue(tasks.get(0) instanceof PopulateTask);
		assertTrue(tasks.get(1) instanceof WorkloadTask);
	}
	@Test
	public void testJobReporter() throws URISyntaxException {
	    File file = Resources.file(ResourceNames.JOB1);
		Job job = Job.create(file);
		OutputConfig rep = job.createOutput();
		JsonObject value = rep.getInclude();
		assertEquals(10, value.getInt("a"));
	}

	@Test
	public void testJobPopulator() throws Exception {
	    File file = Resources.file(ResourceNames.JOB1);
		Job job = Job.create(file);
		List<Task> tasks = job.createTasks();
		Task t = tasks.get(0);
		assertTrue(t instanceof PopulateTask);
		PopulateTask pop = (PopulateTask) t;
		assertNull(pop.findDb());

		PopulateRunner prun = PopulateRunner.create(pop, job);
		DatabaseConfig dbconf = (DatabaseConfig) prun.getTargetConfig();
		assertNotNull(dbconf);
		assertFalse(dbconf.getClassName().isEmpty());
	}
}
