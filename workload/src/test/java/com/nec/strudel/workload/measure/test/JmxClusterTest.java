package com.nec.strudel.workload.measure.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nec.strudel.workload.measure.jmx.JmxCluster;

public class JmxClusterTest {


	@Test
	public void test() {
		JmxCluster cluster = JmxCluster.builder().host("localhost:9999")
		.host("localhost", 9998).build();
		assertEquals(2, cluster.size());
	}
}
