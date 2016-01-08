package com.nec.strudel.workload.com;

import javax.annotation.Nullable;

import com.nec.congenio.ConfigValue;
import com.nec.congenio.Values;

public class CommandFactory {

	public static ProcessCommand create(ConfigValue conf) {
		return ProcessCommand.create(conf.toObject(ProcessCommandConfig.class));
	}

	@Nullable
	public static Command find(ConfigValue conf) {
		if (conf == Values.NONE) {
			return null;
		}
		Workflow flow = conf.findObject("process", Workflow.class);
		if (flow != null) {
			return flow.createCommand();
		}
		return create(conf);
	}
}