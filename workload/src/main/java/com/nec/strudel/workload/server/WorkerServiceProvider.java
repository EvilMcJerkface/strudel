package com.nec.strudel.workload.server;

import com.nec.strudel.workload.cluster.Node;

public interface WorkerServiceProvider {

	WorkerService create(Node node);
}
