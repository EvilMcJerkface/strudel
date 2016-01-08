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
package com.nec.strudel.workload.jobexec.com;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonObject;

import com.nec.strudel.workload.com.Command;
import com.nec.strudel.workload.com.CommandBuilder;
import com.nec.strudel.workload.com.Workflow;
import com.nec.strudel.workload.exec.ReportNames;
import com.nec.strudel.workload.measure.MeasurementConfig;
import com.nec.strudel.workload.measure.jmx.JmxMonitorConfig;
import com.nec.strudel.workload.state.MeasureWorkState;

/**
 * Workload Command Sequence based on MeasurementConfig
 * @author tatemura
 *
 */
public class MeasureWorkloadCommand {
	private final MeasurementConfig measure;
	private final JsonObject input;

	public static Command command(MeasurementConfig measure) {
		return new MeasureWorkloadCommand(measure).createCommand();
	}

	public MeasureWorkloadCommand(MeasurementConfig measure) {
		this.measure = measure;
		this.input = input();
	}

	private JsonObject input() {
		/**
		 * TODO unit
		 */
		return Json.createObjectBuilder()
		.add(ReportNames.VALUE_MEASURE,
			measure.measureTime().getTime(TimeUnit.SECONDS))
		.build();
	}

	public Command createCommand() {
		CommandBuilder builder =
				new CommandBuilder()
		.command(WorkloadCommands.include(MeasurementConfig.TAG_MEASURE,
				input))
		.command(WorkGroupCommands.start())
		.info("workload started")
		.sleep(measure.rampupTime())
		.info("ramp up done")
		.command(WorkGroupCommands.command(MeasureWorkState.COMMAND_MEASURE, input))
		.info("measurement started");
		List<Command> coms = getConcurrentFlows();
		if (!coms.isEmpty()) {
			coms.add(resultCommands());
			builder.parallel(coms.toArray(
					new Command[coms.size()]));
		} else {
			buildResultCommands(builder);
		}
		return builder
				.info("measurement done. stopping workload")
				.command(WorkGroupCommands.stop())
				.info("workload stopped")
				.build();
	}

	private Command resultCommands() {
		CommandBuilder builder =
				new CommandBuilder();
		buildResultCommands(builder);
		return builder.build();
	}
	private void buildResultCommands(
			CommandBuilder builder) {
		builder.sleep(measure.measureTime())
		.command(WorkGroupCommands.waitForState(MeasureWorkState.STATE_DONE))
		.command(WorkloadCommands.fetchReport());
	}

	private List<Command> getConcurrentFlows() {
		List<Command> coms = new ArrayList<Command>();
		Workflow p = measure.getProcess();
		if (p != null) {
			coms.add(WorkloadWorkflow.command(p));
		}
		Map<String, JmxMonitorConfig> monitors = measure.monitors();
		for (JmxMonitorConfig spec : monitors.values()) {
			WorkloadCommand com =
					WorkloadCommands.monitorJmx(spec,
							measure.measureTime(), measure.pollTime());
			coms.add(com);
		}
		return coms;
	}

}
