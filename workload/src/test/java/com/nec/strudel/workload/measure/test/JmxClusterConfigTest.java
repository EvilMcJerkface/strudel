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

import org.junit.Test;

import com.nec.strudel.workload.measure.jmx.JmxCluster;
import com.nec.strudel.workload.measure.jmx.JmxClusterConfig;
import com.nec.strudel.workload.test.JmxConfigs;
import com.nec.strudel.workload.test.ResourceFile;
import com.nec.strudel.workload.test.Resources;

public class JmxClusterConfigTest {


	@Test
	public void test() {
		JmxCluster c = JmxCluster.builder()
				.host("localhost", 9988)
				.host("localhost", 9987)
				.host("yoda1", 9988)
				.host("yoda1", 9987)
				.build();
		JmxCluster[] cs = {
				readCluster(JmxConfigs.CLUSTER000),
				readCluster(JmxConfigs.CLUSTER001),
				readCluster(JmxConfigs.CLUSTER002),
		};
		for (JmxCluster c1 : cs) {
			assertEquals(c, c1);
		}
		
	}

	JmxCluster readCluster(ResourceFile<JmxClusterConfig> f) {
		return Resources.create(f).toCluster();
	}
}
