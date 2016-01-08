package com.nec.strudel.workload.job;

import java.util.Random;

import com.nec.congenio.ConfigValue;
import com.nec.strudel.workload.util.TimeValue;

public interface WorkItem {
	String tagName();
	ConfigValue getConfig();
	ConfigParam getParam();
	String getType();
	int numOfThreads();
	Random getRandom();
	TimeValue startSlackTime();
	String getClassPath();
}
