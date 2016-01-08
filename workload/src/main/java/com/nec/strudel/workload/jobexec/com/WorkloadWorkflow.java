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

import javax.json.JsonObject;

import com.nec.congenio.ConfigValue;
import com.nec.strudel.workload.com.ActionBuilder;
import com.nec.strudel.workload.com.Command;
import com.nec.strudel.workload.com.CommandBuilder;
import com.nec.strudel.workload.com.Workflow;
import com.nec.strudel.workload.measure.jmx.JmxMonitorConfig;
import com.nec.strudel.workload.util.TimeValue;

public final class WorkloadWorkflow {
	private WorkloadWorkflow() {
	}

	public static Command command(Workflow p) {
		p.addBuilder(WorkloadAction.NAME, new WorkloadAction());
		p.addBuilder(MonitorAction.NAME, new MonitorAction());
		return p.createCommand();
	}

	public static class MonitorAction implements ActionBuilder {
		public static final String NAME = "monitor";
		@Override
		public void build(ConfigValue action,
				CommandBuilder builder) {
			JmxMonitorConfig spec =
					action.getObject("resource", JmxMonitorConfig.class);
			TimeValue measureTime =
				TimeValue.seconds(action.getLong("measure"));
			TimeValue pollTime =
				TimeValue.seconds(action.getLong("poll"));
			WorkloadCommand com = WorkloadCommands.monitorJmx(
					spec,
					measureTime, pollTime);
			builder.command(com);
		}

	}
	public static class WorkloadAction implements ActionBuilder {
		public static final String NAME = "work";
		public static final String WORK_START = "start";
		public static final String WORK_STOP = "stop";
		public static final String WORK_COMMAND = "command";
		public static final String WORK_WAIT = "wait";
		public static final String WORK_REPORT = "report";
		@Override
		public void build(ConfigValue action,
				CommandBuilder builder) {
			String com = action.get("do");
			if (WORK_START.equals(com)) {
				builder.command(WorkGroupCommands.start());
			} else if (WORK_STOP.equals(com)) {
				builder.command(WorkGroupCommands.stop());
			} else if (WORK_COMMAND.equals(com)) {
				String name = action.get("cname");
				JsonObject param =
						(JsonObject) action.getJson("arg");
				builder.command(WorkGroupCommands.command(name, param));
			} else if (WORK_WAIT.equals(com)) {
				String state = action.get("state");
				builder.command(WorkGroupCommands.waitForState(state.trim()));
			}
		}
	}
}