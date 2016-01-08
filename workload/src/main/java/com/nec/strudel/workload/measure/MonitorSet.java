package com.nec.strudel.workload.measure;

import java.util.Collections;
import java.util.Map;

import com.nec.congenio.annotation.MapOf;
import com.nec.strudel.workload.measure.jmx.JmxMonitorConfig;

@MapOf(JmxMonitorConfig.class)
public class MonitorSet {
	private final Map<String, JmxMonitorConfig> monitors;

	public MonitorSet(Map<String, JmxMonitorConfig> monitors) {
		this.monitors = monitors;
		/**
		 * NOTE automatic generation of JmxMonitorConfig does not generate
		 * its name - so manual setting is required...
		 */
		for (Map.Entry<String, JmxMonitorConfig> e : monitors.entrySet()) {
			e.getValue().setName(e.getKey());
		}
	}
	public MonitorSet() {
		this.monitors = Collections.emptyMap();
	}
	public Map<String, JmxMonitorConfig> getMonitors() {
		return monitors;
	}
}