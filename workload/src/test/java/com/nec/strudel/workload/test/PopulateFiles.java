package com.nec.strudel.workload.test;

import com.nec.strudel.workload.job.PopulateTask;

public enum PopulateFiles implements ResourceFile<PopulateTask> {
	/**
	 * A KVMap populator using
	 * com.nec.workload.populate.test.Factory
	 */
	POPULATE001("populate001");

	private final String file;
	private PopulateFiles(String file) {
		this.file = file;
	}
	@Override
	public String file() {
		return file;
	}
	@Override
	public Class<PopulateTask> resourceClass() {
		return PopulateTask.class;
	}
}
