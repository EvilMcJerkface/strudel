package com.nec.strudel.workload.test;

import com.nec.strudel.workload.job.WorkloadTask;

public enum WorkloadFiles implements ResourceFile<WorkloadTask> {
	SESSION_WORKLOAD1("workload001"),
	CLASS_WORKLOAD("workload002");

	private final String file;
	private WorkloadFiles(String file) {
		this.file = file;
	}
	@Override
	public String file() {
		return file;
	}
	@Override
	public Class<WorkloadTask> resourceClass() {
		return WorkloadTask.class;
	}
}
