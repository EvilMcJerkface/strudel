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
package com.nec.strudel.workload.job.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.nec.strudel.workload.exec.populate.PopulatePool;
import com.nec.strudel.workload.job.PopulateTask;
import com.nec.strudel.workload.job.PopulateWorkItem;
import com.nec.strudel.workload.job.TaskUtil;
import com.nec.strudel.workload.test.PopulateFiles;
import com.nec.strudel.workload.test.Resources;
import com.nec.strudel.workload.test.kvmap.PopulateFactory;
import com.nec.strudel.workload.test.kvmap.KVMap;

public class PopulateTaskTest {

	@Test
	public void testPopulateTask() {
		PopulateTask pop = Resources.create(PopulateFiles.POPULATE001);
		List<PopulatePool<KVMap, ?>> pools =
				new ArrayList<PopulatePool<KVMap, ?>>();
		for (PopulateWorkItem item : pop.getWorkItems()) {
			int threads = item.numOfThreads();
			assertEquals(4, threads);
			PopulatePool<KVMap, ?> pool =
					PopulatePool.create(item);
			assertEquals(100, pool.getSize());
			pools.add(pool);
		}
		for (PopulateWorkItem item0 : pop.getWorkItems()) {
			item0.setRandomSeed("100");
			PopulateWorkItem item = item0.getConfig().toObject(PopulateWorkItem.class);
			int threads = item.numOfThreads();
			assertEquals(4, threads);
			assertEquals(item0.getName(), item.getName());
			assertEquals(item0.getRandomSeed(), item.getRandomSeed());
			assertEquals(item0.getValidate(), item.getValidate());
			assertEquals(pop.getClassPath(), item.getClassPath());
			assertEquals(item0.getParamConfig().size(), item.getParamConfig().size());
			PopulatePool<KVMap, ?> pool =
					PopulatePool.create(item);
			assertEquals(100, pool.getSize());
		}
		assertEquals(2, pools.size());
		PopulatePool<KVMap, ?> populateX = pools.get(0);
		assertEquals("PopulateX", populateX.getName());
		assertEquals(PopulateFactory.PopulateX.class,
				populateX.getPopulator().getClass());
	}

	@Test
	public void testSeed() {
		long seed = TaskUtil.toSeed("1000");
		assertEquals(1000, seed);
		String t = "abc";
		long seed1 = TaskUtil.toSeed(t);
		assertEquals(t.hashCode(), seed1);
	}

}
