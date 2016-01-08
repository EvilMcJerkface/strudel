package com.nec.strudel.workload.job;


public class ThreadIds {
	private final int nodeId;
	public ThreadIds(int nodeId) {
		this.nodeId = nodeId;
	}
	public int idOf(int threadId) {
		return WorkConfig.createId(nodeId, threadId);
	}
}