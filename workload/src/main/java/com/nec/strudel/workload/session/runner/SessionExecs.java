package com.nec.strudel.workload.session.runner;

import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.workload.session.SessionConfig;
import com.nec.strudel.workload.session.runner.event.SemiOpenSessionExecFactory;

public final class SessionExecs {

	private SessionExecs() {
		// not instantiated
	}
	public static <T> SessionExecFactory<T> createFactory(
			String type,
			String classPath) {
		if (SimpleClosedSessionExecFactory.TYPE.equals(type)) {
			return new SimpleClosedSessionExecFactory<T>();
		} else if (SemiOpenSessionExecFactory.TYPE.equals(type)) {
			return new SemiOpenSessionExecFactory<T>();
		} else {
			throw new ConfigException("unknown session execType: " + type);
		}
	}

	public static <T> SessionExecFactory<T> createFactory(
			SessionConfig<T> conf,
			String classPath) {
		if (conf.getSessionConcurrency() > 0) {
			return new SemiOpenSessionExecFactory<T>();
		}
		return new SimpleClosedSessionExecFactory<T>();
	}
	

}
