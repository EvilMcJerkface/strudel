package com.nec.strudel.workload.env;

import java.util.Properties;

import javax.annotation.Nullable;

import com.nec.congenio.ConfigValue;
import com.nec.strudel.workload.com.Command;
import com.nec.strudel.workload.com.CommandFactory;

public class ExecConfig {
	private ConfigValue conf;
	public ExecConfig(ConfigValue conf) {
		this.conf = conf;
	}
	public ExecConfig() {
		this.conf = null;
	}
	public ConfigValue getValue() {
		return conf;
	}

	public Properties toProperties() {
		if (conf == null) {
			return new Properties();
		}
		return conf.toProperties();
	}
	@Nullable
	public Command toCommand() {
		if (conf == null) {
			return null;
		}
		return CommandFactory.find(conf);
	}
}
