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
package com.nec.strudel.workload.exec.populate;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.param.ParamSequence;
import com.nec.strudel.util.ClassUtil;
import com.nec.strudel.util.Range;
import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.PopulatorFactory;
import com.nec.strudel.workload.job.PopulateWorkItem;
import com.nec.strudel.workload.populator.PackagePopulatorFactory;

/**
 * Pool of populate parameters that is shared
 * by multiple populate threads.
 *
 * @param <T>
 * @param <P>
 */
@ThreadSafe
public class PopulatePool<T, P> {
	private final AtomicInteger count;
	private final Populator<T, P> pop;
	private final int minId;
	private final int maxId;
	private final ParamSequence params;

	public static <T> PopulatePool<T, ?> create(PopulateWorkItem item) {
		return create(item, 0, 0);
	}
	public static <T> PopulatePool<T, ?> create(PopulateWorkItem item,
			int nodeId, int nodeNum) {
		PopulatorFactory<T> factory = createFactory(item);
		Populator<T, ?> pop = factory.create(item.getName());
		if (pop == null) {
			throw new ConfigException("no such populator: "
					+ item.getName());
		}
		Range<Integer> idRange = item.getIdRange();
		ParamSequence pseq;
		if (nodeNum > 1) {
			idRange = idRange.subRange(nodeId, nodeNum);
			pseq = item.getParamConfig().createParamSeq(nodeId, nodeNum);
		} else {
			pseq = item.getParamConfig().createParamSeq();
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		PopulatePool<T, ?> pt = new PopulatePool(pop,
				idRange, pseq);
		return pt;
	}
	public static <T> PopulatorFactory<T> createFactory(PopulateWorkItem item) {
		String classPath = item.getClassPath();
		String factory = item.getFactory();
		if (factory != null && !factory.isEmpty()) {
			return ClassUtil.create(factory, classPath);
		} else {
			return new PackagePopulatorFactory<T>(
					item.getPackageName(), classPath);
		}
	}
	public PopulatePool(Populator<T, P> pop,
	        Range<Integer> idRange, ParamSequence params) {
		this.pop = pop;
		this.minId = idRange.min();
		this.maxId = idRange.max();
		this.params = params;
		this.count = new AtomicInteger(minId);
	}
	public Populator<T, P> getPopulator() {
		return pop;
	}

	public int minId() {
	    return minId;
	}
	public int maxId() {
	    return maxId;
	}
	public int getSize() {
		return maxId - minId;
	}
	public String getName() {
		return pop.getName();
	}
	/**
	 * Gets a new parameter to populate
	 * @param rand
	 * @return null if there is no parameter left
	 */
	@Nullable
	public PopulateParam next(Random rand) {
		int id = count.getAndIncrement();
		if (id < maxId) {
			return new PopulateParam(id,
			        params.nextParam(rand), 
			        new Random(rand.nextLong()));
		}
		return null;
	}
	public int remaining() {
		return maxId - count.get();
	}
}
