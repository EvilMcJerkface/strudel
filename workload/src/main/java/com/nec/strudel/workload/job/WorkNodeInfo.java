package com.nec.strudel.workload.job;

public interface WorkNodeInfo {

	int getNodeId();
	/**
	 * The number of nodes in the cluster.
	 * @return 0 if there is no node (i.e.,
	 * local execution).
	 */
	int getNodeNum();
	/**
	 * Gets the number of threads per node.
	 * @return the number of threads
	 */
	int numOfThreads();
}
