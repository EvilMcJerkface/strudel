/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nec.strudel.workload.job;

import java.util.Random;

import com.nec.congenio.ConfigValue;
import com.nec.strudel.workload.util.TimeValue;

public class WorkloadTask extends Task implements WorkItem {
	public static final String TAG_NAME = "workload";
	public static final String NUM_OF_THREADS = "numOfThreads";
	public static final String START_SLACK = "startSlack";
	public static final String CLASS_PATH = "classPath";
	public static final int DEFAULT_START_SLACK_SEC = 1;
	public static final String TYPE = "type";
	public static final String DEFAULT_TYPE = "session";
	public static final String RANDOM_SEED = "randomSeed";


	private final ConfigValue conf;
	private int numOfThreads;
	private int startSlack;
	private String classPath;
	private String type;
	private String randomSeed;

	public WorkloadTask(ConfigValue conf) {
		this.conf = conf;
		this.numOfThreads = conf.getInt(NUM_OF_THREADS, 1);
		this.startSlack = conf.getInt(START_SLACK,
				DEFAULT_START_SLACK_SEC);
		this.classPath = conf.get(CLASS_PATH, "");
		this.type = conf.get(TYPE, DEFAULT_TYPE);
		this.randomSeed = conf.get(RANDOM_SEED,"");
	}
	@Override
	public String description() {
	    return "workload execution";
	}

	@Override
	public String tagName() {
		return TAG_NAME;
	}
	@Override
	public ConfigValue getConfig() {
		return conf;
	}
	@Override
	public int numOfThreads() {
	    return numOfThreads;
	}
	@Override
	public TimeValue startSlackTime() {
		return TimeValue.seconds(startSlack);
	}
	@Override
	public String getClassPath() {
		return classPath;
	}

	@Override
	public String getType() {
		return type;
	}
	@Override
	public Random getRandom() {
        return TaskUtil.getRandom(randomSeed);
	}

	@Override
	public ConfigParam getParam() {
		return ConfigParam.create(conf);
	}

}
