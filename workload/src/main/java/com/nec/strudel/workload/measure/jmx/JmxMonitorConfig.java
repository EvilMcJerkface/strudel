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
