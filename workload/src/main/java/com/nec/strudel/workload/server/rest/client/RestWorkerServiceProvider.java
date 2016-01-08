package com.nec.strudel.workload.server.rest.client;

import com.nec.strudel.workload.cluster.Node;
import com.nec.strudel.workload.server.WorkerService;
import com.nec.strudel.workload.server.WorkerServiceProvider;

public class RestWorkerServiceProvider implements WorkerServiceProvider {

	@Override
	public WorkerService create(Node node) {
		return new RestWorkerService(node.getUrl());
	}

}
