package com.nec.strudel.workload.job;

import javax.annotation.Nullable;

import com.nec.congenio.ConfigValue;
import com.nec.congenio.Values;

public class ConfigParam {

	public static ConfigParam empty() {
		return new ConfigParam(Values.NONE);
	}
	public static ConfigParam create(ConfigValue conf) {
		return new ConfigParam(conf);
	}

	private final ConfigValue conf;
	public ConfigParam(ConfigValue conf) {
		this.conf = conf;
	}

	public <T> T getObject(String name, Class<T> cls) {
		return conf.getObject(name, cls);
	}

	@Nullable
	public String findString(String name) {
		return conf.find(name);
	}

	public String getString(String name) {
		return conf.get(name);
	}

	public int getInt(String name, int defaultValue) {
		return conf.getInt(name, defaultValue);
	}
	public int getInt(String name) {
		return conf.getInt(name);
	}
	public double getDouble(String name, double defaultValue) {
		return conf.getDouble(name, defaultValue);
	}
}
