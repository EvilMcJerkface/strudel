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