package com.nec.strudel.workload.job;

import java.util.HashMap;
import java.util.Map;

import com.nec.congenio.ConfigValue;
import com.nec.congenio.Values;
import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.target.impl.DatabaseConfig;
import com.nec.strudel.workload.cluster.Node;
import com.nec.strudel.workload.util.TimeValue;

/**
 * A unit of work given to a worker node
 * <pre>
 * "work" : {
 *   "node" : Node,
 *   "database" : Database,
 *   ("Workload" | "Populate") : WorkItem
 * }
 * </pre>
 */
public class WorkRequest {
	private static final Map<String, Class<? extends WorkItem>> ITEM_CLASSES =
			new HashMap<String, Class<? extends WorkItem>>();
	static {
		ITEM_CLASSES.put(WorkloadTask.TAG_NAME, WorkloadTask.class);
		ITEM_CLASSES.put(PopulateWorkItem.TAG_NAME, PopulateWorkItem.class);
	}
	static WorkItem extractItem(ConfigValue conf) {
		for (String name : ITEM_CLASSES.keySet()) {
			WorkItem item = conf.findObject(name, ITEM_CLASSES.get(name));
			if (item != null) {
				return item;
			}
		}
		throw new ConfigException("work item not found in request");
	}

	private final Node node;
	private final WorkItem item;
	private final DatabaseConfig database;


	public static WorkRequest createLocal(
			WorkItem item, DatabaseConfig dbConfig) {
		return new WorkRequest(Node.empty(), item, dbConfig);
	}

	public WorkRequest(Node node,
			WorkItem item, DatabaseConfig dbConfig) {
		this.node = node;
		this.item = item;
		this.database = dbConfig;
	}

	public Node getNode() {
		return node;
	}
	public int getNodeId() {
		return node.getId();
	}
	public int getNodeNum() {
		return node.getNum();
	}
	public WorkItem getWorkItem() {
		return item;
	}
	public String getType() {
		return item.getType();
	}
	public String getClassPath() {
		return item.getClassPath();
	}

	public WorkConfig getConfig() {
		return new WorkConfig(
				node.getId(),
				node.getNum(),
				item, database);

	}

	public TimeValue startSlackTime() {
		return item.startSlackTime();
	}

	public String toString() {
		return toXMLString();
	}

	public static final String TAG_NAME = "work";
	public static final String NODE = "node";
	public static final String DATABASE = "database";
	public String toXMLString() {
		return Values.builder(TAG_NAME)
		.add(NODE, node)
		.add(item.tagName(), item.getConfig())
		.add(DATABASE, database)
		.toXMLString();
	}
	public static WorkRequest parse(String input) {
		ConfigValue conf = Values.parseValue(input);
		Node node = conf.getObject(NODE, Node.class);
		WorkItem item = extractItem(conf);
		DatabaseConfig dbConfig =
				conf.getObject(DATABASE, DatabaseConfig.class);
		dbConfig.setContextClassPath(item.getClassPath());
		return new WorkRequest(node, item, dbConfig);
	}

}
