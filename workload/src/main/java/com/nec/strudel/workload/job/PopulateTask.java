package com.nec.strudel.workload.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.nec.strudel.target.impl.DatabaseConfig;
import com.nec.strudel.workload.cluster.Cluster;

/**
 * <pre>
 * "Populate" : {
 *     "cluster"? : CLUSTER,
 *     "database"? : DATABASE,
 *     "nodeNum"? : int (0),
 *     "process" : [PopulateWorkItem],
 *     ("factory" | "package") : string,
 *     "numOfThreads"? : int (1),
 *     "classPath"? : string (""),
 *     "startSlack"? : int (1),
 *     "randomSeed"? : long (null)
 *     "validate"? : boolean (false),
 * }
 * </pre>
 *
 */
public class PopulateTask extends Task {
	public static final String TAG_NAME = "populate";
	public static final int DEFAULT_START_SLACK_SEC = 1;
	private String factory = "";
	private String packageName = "";
	private String randomSeed = "";
	private String classPath = "";
	private boolean validate;
	private int nodeNum = 0;
	private int numOfThreads = 0;
	private int startSlack = PopulateTask.DEFAULT_START_SLACK_SEC;
	private PopulateWorkItem[] process = new PopulateWorkItem[0];

	private Cluster cluster = null;
	private DatabaseConfig database = null;

	public PopulateTask() {
	}
	@Override
	public String description() {
	    return "data population";
	}

	public List<PopulateWorkItem> getWorkItems() {
		List<PopulateWorkItem> result =
				new ArrayList<PopulateWorkItem>();
		Random rand = getRandom();
		for (PopulateWorkItem item : process) {
			PopulateWorkItem task =
					createPopulateWorkItem(item, rand);
			result.add(task);
			
		}
		return result;
	}
	private PopulateWorkItem createPopulateWorkItem(
			PopulateWorkItem item, Random rand) {

		if (!randomSeed.isEmpty()) {
			item.setRandomSeed(Long.toString(rand.nextLong()));
		}
		item.setFactory(factory);
		item.setPackageName(packageName);
		if (item.getClassPath().isEmpty()) {
			item.setClassPath(classPath);
		}
		if (item.getNumOfThreads() == 0) {
			item.setNumOfThreads(numOfThreads);
		}
		if (validate) {
			item.setValidate(validate);
		}
		if (item.getStartSlack() == 0) {
			item.setStartSlack(startSlack);
		}
		return item;
		
	}
	@Nullable
	public Cluster findCluster() {
		return cluster;
	}
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	@Nullable
	public DatabaseConfig findDb() {
		return database;
	}
	public void setDatabase(DatabaseConfig db) {
		this.database = db;
	}

	public int getNodeNum() {
		return nodeNum;
	}
	public void setNodeNum(int nodeNum) {
		this.nodeNum = nodeNum;
	}
	public int getNumOfThreads() {
		return numOfThreads;
	}
	public void setNumOfThreads(int numOfThreads) {
		this.numOfThreads = numOfThreads;
	}
	public void setFactory(String factory) {
		this.factory = factory;
	}
	public String getFactory() {
		return factory;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getPackageName() {
		return packageName;
	}

	public String getClassPath() {
		return classPath;
	}
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
	public boolean getValidate() {
		return validate;
	}
	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public int getStartSlack() {
		return startSlack;
	}
	public void setStartSlack(int startSlack) {
		this.startSlack = startSlack;
	}
	public void setProcess(PopulateWorkItem[] process) {
		this.process = process;
	}
	public void addItem(PopulateWorkItem item) {
		PopulateWorkItem[] newProcess = Arrays.copyOf(process, process.length + 1);
		newProcess[process.length] = item;
		this.process = newProcess;
	}

	public void setRandomSeed(String randomSeed) {
		this.randomSeed = randomSeed;
	}
	public String getRandomSeed() {
		return randomSeed;
	}

	public Random getRandom() {
		if (!randomSeed.isEmpty()) {
			return new Random(TaskUtil.toSeed(randomSeed));
		} else {
			return new Random();
		}
	}

}
