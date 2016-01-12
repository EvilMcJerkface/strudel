package com.nec.strudel.workload.test;

import com.nec.strudel.workload.measure.jmx.JmxClusterConfig;
import com.nec.strudel.workload.measure.jmx.JmxMonitorConfig;

public final class JmxConfigs {

	private JmxConfigs() {
	}

	public static final ResourceFile<JmxMonitorConfig> MONITOR001 =
			Resources.of("jmx/jmxmonitor001", JmxMonitorConfig.class);
	public static final ResourceFile<JmxClusterConfig> CLUSTER000 =
			Resources.of("jmx/jmxcluster000", JmxClusterConfig.class);
	public static final ResourceFile<JmxClusterConfig> CLUSTER001 =
			Resources.of("jmx/jmxcluster001", JmxClusterConfig.class);
	public static final ResourceFile<JmxClusterConfig> CLUSTER002 =
			Resources.of("jmx/jmxcluster002", JmxClusterConfig.class);
}
