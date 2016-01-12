package com.nec.strudel.workload.measure.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nec.strudel.workload.measure.ClusterAggregation;
import com.nec.strudel.workload.measure.jmx.JmxCluster;
import com.nec.strudel.workload.measure.jmx.JmxClusterConfig;
import com.nec.strudel.workload.measure.jmx.JmxMonitorConfig;
import com.nec.strudel.workload.measure.jmx.MonitorValue;
import com.nec.strudel.workload.test.JmxConfigs;
import com.nec.strudel.workload.test.Resources;

public class JmxMonitorConfigTest {

	@Test
	public void test() throws Exception {
		JmxMonitorConfig spec = Resources.create(JmxConfigs.MONITOR001);
		JmxClusterConfig cc = spec.getCluster();
		assertNotNull(cc);
		JmxCluster cluster = cc.toCluster();
		assertEquals(2, cluster.size());
		assertEquals("monitor", spec.getName());
		MonitorValue[] values = spec.getValues();
		assertEquals(2, values.length);
		MonitorValue v1 = values[0];
		assertEquals("throughput", v1.getName());
		assertEquals("TransactionsPerSec", v1.getAttr());
		assertEquals("com.nec.workload:type=Test", v1.getObject());
		
		assertEquals(
				ClusterAggregation.get(ClusterAggregation.Op.SUM),
				 ClusterAggregation.get(v1.getAggr()));
	}
}
