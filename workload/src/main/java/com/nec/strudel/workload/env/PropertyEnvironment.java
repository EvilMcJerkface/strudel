package com.nec.strudel.workload.env;

import java.util.Properties;

import com.nec.strudel.exceptions.ConfigException;

public abstract class PropertyEnvironment implements Environment {

	public abstract void start(Properties props);
	public abstract void stop(Properties props);
	public void startSuite(Properties props) {
		throw new ConfigException("startSuite not supported");
	}
	public void stopSuite(Properties props) {
		throw new ConfigException("stopSuite not supported");
	}

	@Override
	public final void start(ExecConfig conf) {
		Properties prop = conf.toProperties();
		start(prop);
	}

	@Override
	public final void stop(ExecConfig conf) {
		Properties prop = conf.toProperties();
		stop(prop);
	}

	@Override
	public void startSuite(ExecConfig conf) {
		startSuite(conf.toProperties());
	}
	@Override
	public void stopSuite(ExecConfig conf) {
		stopSuite(conf.toProperties());
	}

}
