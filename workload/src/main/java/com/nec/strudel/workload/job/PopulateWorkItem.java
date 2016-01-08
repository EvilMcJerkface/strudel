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
import com.nec.congenio.Values;
import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.param.ParamConfig;
import com.nec.strudel.util.Range;
import com.nec.strudel.workload.util.TimeValue;

/**
 * <pre>
 * "Populate" : {
 *   "name" : string,
 *   "min" : int,
 *   ("size" | "max") : int,
 *   "params" : ParamConfig,
 *
 *   "factory" : string ("") 
 *   "packageName" : string (""),
 *   "numOfThreads" : int (0),
 *   "validate" : boolean (false),
 *   "classPath" : string (""),
 *   "startSlack" : int (0),
 *   "randomSeed" : string ("")
 * }
 * </pre>
 *
 */
public class PopulateWorkItem implements WorkItem {
	private static final Random RAND = new Random(System.nanoTime());
	public static final String TYPE = "populate";
	public static final String TAG_NAME = "Populate";
	private Random rand = newRandom();

	private String name = "";
	private boolean validate = false;
	private int numOfThreads = 0;
	private int startSlack = 0;
	private String classPath = "";
	private String factory = "";
	private String packageName = "";
	private ParamConfig params = ParamConfig.empty();
	private String randomSeed = "";
	private int min = -1;
	private int max = -1;
	private int size = -1;

	private static Random newRandom() {
		synchronized (RAND) {
			return new Random(RAND.nextLong());
		}
	}

	public PopulateWorkItem() {
	}
	@Override
	public String tagName() {
		return TAG_NAME;
	}

	@Override
	public ConfigValue getConfig() {
		ConfigValue v = Values.create(this);
		return Values.builder(TAG_NAME, v)
				.add("params", params.getConfig())
				.build();
	}
	@Override
	public String getType() {
		return TYPE;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Range<Integer> getIdRange() {
		return createRange();
	}

	public boolean isValidate() {
		return validate;
	}
	public boolean getValidate() {
		return validate;
	}
	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	@Override
	public int numOfThreads() {
		return numOfThreads;
	}
	public int getNumOfThreads() {
		return numOfThreads;
	}
	public void setNumOfThreads(int numOfThreads) {
		this.numOfThreads = numOfThreads;
	}

	@Override
	public TimeValue startSlackTime() {
		return TimeValue.seconds(startSlack);
	}
	public int getStartSlack() {
		return startSlack;
	}
	public void setStartSlack(int startSlack) {
		this.startSlack = startSlack;
	}

	@Override
	public synchronized Random getRandom() {
		return new Random(rand.nextLong());
	}

	@Override
	public ConfigParam getParam() {
		return ConfigParam.empty();
	}

	public ParamConfig getParamConfig() {
		return params;
	}
	public void setParams(ParamConfig params) {
		this.params = params;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

	public String getFactory() {
		return factory;
	}
	public void setFactory(String factory) {
		this.factory = factory;
	}

	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getRandomSeed() {
		return randomSeed;
	}
	public void setRandomSeed(String randomSeed) {
		if (randomSeed != null && !randomSeed.isEmpty()) {
			rand = new Random(TaskUtil.toSeed(randomSeed));
		}
		this.randomSeed = randomSeed;
	}

	@Override
	public String getClassPath() {
		return classPath;
	}
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	Range<Integer> createRange() {
	    if (size > 0) {
	        if (min != -1) {
	            return Range.range(min, min + size);
	        }
	        return Range.range(0, size);
	    }
        if (min == -1 || max == -1) {
            throw new ConfigException(
            "size or range not specified");
        }
        return Range.range(min, max);
		
	}

}
