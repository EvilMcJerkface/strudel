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
package com.nec.strudel.workload.measure;

import java.util.Map;

import javax.annotation.Nullable;

import com.nec.strudel.workload.com.Workflow;
import com.nec.strudel.workload.measure.jmx.JmxMonitorConfig;
import com.nec.strudel.workload.util.TimeValue;

/**
 * <pre>
 * "Measure" : {
 *   "rampup"? : int (0),
 *   "duration"? : int (0),
 *   "poll"? : int (1)
 *   "Process"? [workflow],
 *   "monitors"? : {
 *   	NAME : JmxMonitorSpecCreator
 *   },
 * }
 * </pre>
 */
public class MeasurementConfig {

	private int rampup;
	private int duration;
	private int poll;
	private MonitorSet monitors = new MonitorSet();
	private Workflow process;

	public static final String TAG_MEASURE = "measure";

	public MeasurementConfig() {
	}

	/**
	 * Gets the ramp-up duration.
	 */
	public TimeValue rampupTime() {
		return TimeValue.seconds(rampup);
	}

	/**
	 * Gets the measure duration.
	 */
	public TimeValue measureTime() {
		return TimeValue.seconds(duration);
	}
	public TimeValue pollTime() {
		return TimeValue.seconds(poll);
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setPoll(int poll) {
		this.poll = poll;
	}

	public void setRampup(int rampup) {
		this.rampup = rampup;
	}

	public void setMonitors(MonitorSet monitors) {
		this.monitors = monitors;
	}

	public Map<String, JmxMonitorConfig> monitors() {
		return monitors.getMonitors();
	}

	@Nullable
	public Workflow getProcess() {
		return process;
	}
	public void setProcess(Workflow process) {
		this.process = process;
	}

}
