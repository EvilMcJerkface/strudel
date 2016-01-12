package com.nec.strudel.workload.test;

import com.nec.strudel.workload.session.SessionConfig;

@SuppressWarnings("rawtypes")
public enum SessionFiles implements ResourceFile<SessionConfig> {
	/**
	 * Example of a random session
	 */
	SESSION001("session/session001"),
	/**
	 * Example of a Markov session
	 */
	SESSION002("session/session002");
	
	private final String file;
	SessionFiles(String file) {
		this.file = file;
	}
	public String file() {
		return file;
	}
	public Class<SessionConfig> resourceClass() {
		return SessionConfig.class;
	}


}
