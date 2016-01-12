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
package com.nec.strudel.workload.measure.test;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.nec.strudel.workload.com.Workflow;
import com.nec.strudel.workload.measure.MeasurementConfig;
import com.nec.strudel.workload.measure.jmx.JmxMonitorConfig;
import com.nec.strudel.workload.measure.jmx.MonitorValue;
import com.nec.strudel.workload.test.MeasurementFiles;
import com.nec.strudel.workload.test.Resources;

public class MeasurementConfigTest {

	@Test
	public void testMeasurementConfig() {
		MeasurementConfig conf = Resources.create(MeasurementFiles.MEASURE001);
		assertEquals(TimeUnit.SECONDS.toMillis(1200),
				conf.measureTime().toMillis());
		assertEquals(TimeUnit.SECONDS.toMillis(120),
				conf.rampupTime().toMillis());
		assertEquals(TimeUnit.SECONDS.toMillis(5),
				conf.pollTime().toMillis());
		assertTrue(conf.monitors().isEmpty());
		assertNull(conf.getProcess());
	}
	@Test
	public void testMeasurementWithMonitor() {
		MeasurementConfig conf = Resources.create(MeasurementFiles.MEASURE002);
		Map<String, JmxMonitorConfig> monitors = conf.monitors();
		assertEquals(2, monitors.size());
		JmxMonitorConfig mon = monitors.get("test");
		assertNotNull(mon);
		assertEquals("test", mon.getName());
		MonitorValue[] vals = mon.getValues();
		assertEquals(2, vals.length);
		assertEquals("response_time", vals[0].getName());
	}
	@Test
	public void testMeasurementWithProcess() {
		MeasurementConfig conf = Resources.create(MeasurementFiles.MEASURE003);
		Workflow process = conf.getProcess();
		assertNotNull(process);
	}
}
