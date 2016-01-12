package com.nec.strudel.workload.job.test;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import javax.json.JsonObject;

import org.junit.Test;

import com.nec.strudel.workload.job.Job;
import com.nec.strudel.workload.job.PopulateTask;
import com.nec.strudel.workload.job.Task;
import com.nec.strudel.workload.job.WorkloadTask;
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
}
