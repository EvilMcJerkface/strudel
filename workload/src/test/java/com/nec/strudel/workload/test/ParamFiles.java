package com.nec.strudel.workload.test;

import com.nec.strudel.param.ParamConfig;

public enum ParamFiles implements ResourceFile<ParamConfig> {
	PARAM001("param/testparam001"),
	PARAM002("param/testparam002");

	private final String file;
	private ParamFiles(String file) {
		this.file = file;
	}
	public String file() {
		return file;
	}
	@Override
	public Class<ParamConfig> resourceClass() {
		return ParamConfig.class;
	}
}
