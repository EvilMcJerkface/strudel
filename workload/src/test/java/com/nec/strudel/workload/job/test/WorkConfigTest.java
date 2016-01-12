package com.nec.strudel.workload.job.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nec.strudel.workload.job.WorkConfig;

public class WorkConfigTest {


	@Test
	public void testId() {
		int nodeId = 7;
		int threadId = 13;
		int id = WorkConfig.createId(nodeId, threadId);
		assertEquals(nodeId, WorkConfig.nodeIdOf(id));
		assertEquals(threadId, WorkConfig.threadIdOf(id));
	}

}
