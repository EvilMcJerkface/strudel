package com.nec.strudel.workload.test;

import com.nec.strudel.workload.cluster.Cluster;

public enum ClusterFiles implements ResourceFile<Cluster> {
	CLUSTER000("cluster/cluster000"),
	CLUSTER001("cluster/cluster001");

	private final String file;
	private ClusterFiles(String file) {
		this.file = file;
	}
	@Override
	public String file() {
		return file;
	}
	@Override
	public Class<Cluster> resourceClass() {
		return Cluster.class;
	}

}
