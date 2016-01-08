package com.nec.strudel.workload.measure.jmx;

import javax.annotation.Nullable;

import com.nec.strudel.workload.measure.ResultAggregation;
import com.nec.strudel.workload.measure.ResultAggregationFactory;

/**
 * <pre>
 * {
 *   "name" : string,
 *   "cluster" : JmxClusterConfig,
 *   "out"? : string ("avg"),
 *   "values" : [MonitorValue]
 * }
 * MonitorValue: {
 *  "object" : string,
 *  "attr" : string,
 *  "aggr" : string,
 *  "name"? : string
 * }
 * </pre>
 *
 */
public class JmxMonitorConfig {

	private String name;
	private JmxClusterConfig cluster = new JmxClusterConfig();
	private MonitorValue[] values = new MonitorValue[0];
	private String out = ResultAggregationFactory.TYPE_AVG;

	public JmxMonitorConfig() {
	}

	@Nullable
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JmxClusterConfig getCluster() {
		return cluster;
	}

	public void setCluster(JmxClusterConfig cluster) {
		this.cluster = cluster;
	}

	public String getOut() {
		return out;
	}
	public void setOut(String out) {
		this.out = out;
	}

	public MonitorValue[] getValues() {
		return values;
	}
	public void setValues(MonitorValue[] values) {
		this.values = values;
	}

	public ResultAggregation createAggr() {
		return new ResultAggregationFactory(out).create();
	}

}
