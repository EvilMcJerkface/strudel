package com.nec.strudel.workload.job;

import java.util.Random;

import com.nec.strudel.target.TargetConfig;

public class WorkConfig implements WorkNodeInfo {
	private final int nodeId;
	private final int nodeNum;
	private final TargetConfig targetConfig;
	private final WorkItem item;
	public WorkConfig(int nodeId,
			int nodeNum, WorkItem item,
			TargetConfig targetConfig) {
		this.nodeId = nodeId;
		this.nodeNum = nodeNum;
		this.item = item;
		this.targetConfig = targetConfig;
	}
	public int getNodeId() {
		return nodeId;
	}

	/**
	 * The number of nodes in the cluster.
	 * @return 0 if there is no node (i.e.,
	 * local execution).
	 */
	public int getNodeNum() {
		return nodeNum;
	}

	public TargetConfig getTargetConfig() {
		return targetConfig;
	}

	public String getClassPath() {
		return item.getClassPath();
	}

	public ConfigParam getParam() {
		return item.getParam();
	}
	public WorkItem getItem() {
		return item;
	}

	/**
	 * Gets the number of threads per node.
	 * @return the number of threads
	 */
	public int numOfThreads() {
		return item.numOfThreads();
	}

	public Random getRandom() {
		return item.getRandom();
	}

	private static final int SHORT = 16;
    private static final int SHORT_MASK = 0xFFFF;
    public static int createId(int nodeId, int threadId) {
		return (nodeId & SHORT_MASK) << SHORT | (threadId & SHORT_MASK);
	}
    public static int nodeIdOf(int id) {
    	return id >> SHORT;
    }
    public static int threadIdOf(int id) {
    	return (id & SHORT_MASK);
    }
}
