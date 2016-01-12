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

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.nec.strudel.management.ManagementService;
import com.nec.strudel.management.NoService;
import com.nec.strudel.target.impl.DatabaseConfig;
import com.nec.strudel.workload.cluster.Node;
import com.nec.strudel.workload.exec.WorkloadFactory;
import com.nec.strudel.workload.exec.batch.BatchExec;
import com.nec.strudel.workload.exec.batch.WorkThread;
import com.nec.strudel.workload.job.ConfigParam;
import com.nec.strudel.workload.job.WorkConfig;
import com.nec.strudel.workload.job.WorkRequest;
import com.nec.strudel.workload.job.WorkloadTask;
import com.nec.strudel.workload.measure.MeasurementConfig;
import com.nec.strudel.workload.session.MarkovSession;
import com.nec.strudel.workload.session.Session;
import com.nec.strudel.workload.session.SessionConfig;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.session.runner.AbstractSessionRunner;
import com.nec.strudel.workload.test.DBFiles;
import com.nec.strudel.workload.test.Resources;
import com.nec.strudel.workload.test.WorkloadFiles;
import com.nec.strudel.workload.util.TimeValue;

public class WorkloadTaskTest {

	@Test
	public void testWorkloadTask() {
	    DatabaseConfig dbConf = Resources.create(DBFiles.DB_TKVS);
        WorkloadTask workload = Resources.create(WorkloadFiles.SESSION_WORKLOAD1);
		Node node = Node.empty();
		WorkRequest wreq = new WorkRequest(node, workload, dbConf);
		WorkConfig wconf = wreq.getConfig();
		assertEquals("session", wreq.getType());
		assertEquals(0, wconf.getNodeNum());
		assertEquals(100, wconf.numOfThreads());
		MeasurementConfig measure =
				workload.getConfig().findObject(
						MeasurementConfig.TAG_MEASURE, MeasurementConfig.class);
		TimeValue ramp = measure.rampupTime();
		assertEquals(120, ramp.getTime(TimeUnit.SECONDS));
		assertEquals(1200, measure.measureTime()
					.getTime(TimeUnit.SECONDS));
		assertEquals("auction", dbConf.getName());
		assertEquals("tkvs", dbConf.getType());
		ConfigParam param = wconf.getParam();
		@SuppressWarnings("unchecked")
		SessionConfig<Object> sxml = param.getObject(SessionConfig.SESSION, SessionConfig.class);
		SessionFactory<Object> sf = sxml.createSessionFactory("");
		Session<Object> session = sf.create();
		assertTrue(session instanceof MarkovSession);
		MarkovSession<Object> ms = (MarkovSession<Object>) session;
		assertEquals(120, ms.getMaxTime());
		assertEquals(60, ms.getMinTime());
	}

	@Test
	public void testWorkloadFactory() {
	    DatabaseConfig dbConf = Resources.create(DBFiles.DB_TKVS);
        WorkloadTask workload = Resources.create(WorkloadFiles.SESSION_WORKLOAD1);
		WorkloadFactory factory = new WorkloadFactory(
				WorkRequest.createLocal(workload, dbConf));
		WorkThread[] works = 
				BatchExec.findWorkThreads(factory.createExec(mx()));
		assertEquals(100, works.length);
		for (WorkThread w : works) {
			assertTrue(w instanceof AbstractSessionRunner);
		}
	}
	@Test
	public void testWorkloadFactoryWithClass() {
	    DatabaseConfig dbConf = Resources.create(DBFiles.DB_TKVS);
		WorkloadTask workload =
				Resources.create(WorkloadFiles.CLASS_WORKLOAD);
		WorkloadFactory factory = new WorkloadFactory(
				WorkRequest.createLocal(workload, dbConf));
		WorkThread[] works =
				BatchExec.findWorkThreads(factory.createExec(mx()));
		assertEquals(100, works.length);
		for (WorkThread w : works) {
			assertTrue(w instanceof DummyWorkload.DummyWorkThread);
		}

	}

	protected ManagementService mx() {
		return new NoService();
	}
}
