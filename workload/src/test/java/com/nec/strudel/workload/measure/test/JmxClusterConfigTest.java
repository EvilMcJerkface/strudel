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
