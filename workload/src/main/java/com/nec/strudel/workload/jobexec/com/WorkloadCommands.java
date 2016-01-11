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

import javax.json.Json;
import javax.json.JsonObject;

import com.nec.strudel.metrics.Output;
import com.nec.strudel.metrics.Report;
import com.nec.strudel.workload.com.CommandContext;
import com.nec.strudel.workload.com.CommandResult;
import com.nec.strudel.workload.jobexec.WorkExecContext;
import com.nec.strudel.workload.jobexec.WorkloadResult;
import com.nec.strudel.workload.measure.JmxMonitorFactory;
import com.nec.strudel.workload.measure.ResourceMonitor;
import com.nec.strudel.workload.measure.jmx.JmxMonitorConfig;
import com.nec.strudel.workload.out.OutputSet;
import com.nec.strudel.workload.util.TimeUtil;
import com.nec.strudel.workload.util.TimeValue;
import com.nec.strudel.workload.worker.WorkGroup;

public final class WorkloadCommands {

	private WorkloadCommands() {
	}

	public static WorkloadCommand fetchReport() {
		return new GetReportCommand();
	}
	public static WorkloadCommand include(String name, JsonObject input) {
		return new IncludeCommand(name, input);
	}
	public static WorkloadCommand monitorJmx(
			JmxMonitorConfig spec,
			TimeValue measureTime, TimeValue pollTime) {
		return new PollJmxCommand(spec,
				measureTime.toMillis(), pollTime.toMillis());
	}
	static class PollJmxCommand extends PollCommand {
		private final JmxMonitorConfig spec;
		private ResourceMonitor mon;
		public PollJmxCommand(
				JmxMonitorConfig spec,
				long measureTime, long pollTime) {
			super(measureTime, pollTime);
			this.spec = spec;
		}
		@Override
		protected void start(CommandContext ctxt) {
			mon = JmxMonitorFactory.create(spec);
		}
		@Override
		protected void poll(CommandContext ctxt) throws InterruptedException {
			mon.process(ctxt);
		}
		@Override
		protected void done(CommandContext ctxt) {
			mon.close();
			JsonObject value = Json.createObjectBuilder()
					.add(spec.getName(), mon.getResult())
					.build();
			((WorkExecContext) ctxt).result(new WorkloadResult(value));
		}


		@Override
		public OutputSet outputs() {
			return OutputSet.builder().add(spec.getName(),
					Output.names(spec.getName()))
					.build();
		}

	}
	abstract static class PollCommand implements WorkloadCommand {
		private final long measureTime;
		private final long pollTime;
		public PollCommand(long measureTime, long pollTime) {
			this.measureTime = measureTime;
			this.pollTime = pollTime;
		}
		@Override
		public CommandResult run(CommandContext ctxt)
				throws InterruptedException {
			start(ctxt);
			long endTime = System.currentTimeMillis()
					+ measureTime;

			Thread.sleep(pollTime);

			while (endTime > System.currentTimeMillis()) {
				long startTime = System.currentTimeMillis();
				poll(ctxt);
				long time = System.currentTimeMillis()
						- startTime;
				long remain = pollTime - time;
				if (remain > 0) {
					Thread.sleep(remain);
				} else {
					ctxt.logger().warn("polling takes "
					+ TimeUtil.formatTimeMS(time)
					+ " > interval ("
					+ TimeUtil.formatTimeMS(pollTime)
					+ ")");
				}
			}
			done(ctxt);
			return CommandResult.success();
		}
		protected abstract void poll(CommandContext ctxt) throws InterruptedException;
		protected abstract void start(CommandContext ctxt);
		protected abstract void done(CommandContext ctxt);
	}
	public static class IncludeCommand implements WorkloadCommand {
		private final String name;
		private final JsonObject input;
		public IncludeCommand(String name, JsonObject input) {
			this.name = name;
			this.input = input;
		}
		@Override
		public CommandResult run(CommandContext ctxt)
				throws InterruptedException {
			WorkExecContext wc = (WorkExecContext) ctxt;
			wc.result(new WorkloadResult(input));
			return CommandResult.success();
		}
	
		@Override
		public OutputSet outputs() {
			return OutputSet.builder()
					.add(name, Output.referenceTo(input))
					.build();
		}
	}
	public static class GetReportCommand implements WorkloadCommand {
		private Report report = Report.none();
		public GetReportCommand() {
		}
		@Override
		public CommandResult run(CommandContext ctxt)
				throws InterruptedException {
			WorkExecContext wc = (WorkExecContext) ctxt;
			WorkGroup wg = wc.workGroup();
			report = wg.getReport();
			wc.result(new WorkloadResult(
					report.getValues(),
					report.getWarns()));
			return CommandResult.success();
		}
	
		@Override
		public OutputSet outputs() {
			return OutputSet.empty();
		}
	}

}
