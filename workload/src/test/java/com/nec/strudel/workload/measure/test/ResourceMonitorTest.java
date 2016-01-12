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
package com.nec.strudel.workload.measure.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.json.JsonObject;

import org.junit.Test;

import com.nec.congenio.json.JsonValueUtil;
import com.nec.strudel.workload.com.Caller;
import com.nec.strudel.workload.measure.ClusterAggregation;
import com.nec.strudel.workload.measure.ResourceMonitor;
import com.nec.strudel.workload.measure.ResultAggregationFactory;
import com.nec.strudel.workload.measure.ResourceMonitor.ValueCollector;
import com.nec.strudel.workload.measure.ResourceMonitor.ValueFetcher;

public class ResourceMonitorTest {


	@Test
	public void test() throws Exception {
		int id1 = 1;
		int id2 = 2;
		List<ValueFetcher> fetchers = Arrays.asList(
				(ValueFetcher) new TestValueFetcher(id1),
				(ValueFetcher) new TestValueFetcher(id2)
				);
		Map<Object, ValueCollector> collectors = new HashMap<Object, ValueCollector>();
		ClusterAggregation sum = ClusterAggregation.get("sum");
		collectors.put("a", new ValueCollector("aa", sum, fetchers.size()));
		collectors.put("b", new ValueCollector("bb", sum, fetchers.size()));
		ResourceMonitor mon = new ResourceMonitor(fetchers, collectors,
				new ResultAggregationFactory(ResultAggregationFactory.TYPE_AVG)
				.create());
		Caller caller = new SeqCaller();
		int c = 2;
		for (int i = 0; i < c * 2 + 1; i++) {
			mon.process(caller);
		}
		JsonObject val = (JsonObject) mon.getResult();
		int aa = (id1 + id2) + 2 * c;
		int bb = aa + 2 * 10;
		assertEquals(JsonValueUtil.create((double) aa), val.get("aa"));
		assertEquals(JsonValueUtil.create((double) bb), val.get("bb"));
	}
	static class SeqCaller implements Caller {

		@Override
		public <T> List<Future<T>> call(List<? extends Callable<T>> calls) {
			List<Future<T>> result = new ArrayList<Future<T>>();
			for (Callable<T> c : calls) {
				try {
					T val = c.call();
					result.add(new TestFuture<T>(val));
				} catch (Exception e) {
					result.add(new TestFuture<T>(
							new ExecutionException(e)));
				}
			}
			return result;
		}
	}
	static class TestFuture<T> implements Future<T> {
		private final T value;
		private final ExecutionException ex;
		public TestFuture(T value) {
			this.value = value;
			this.ex = null;
		}
		public TestFuture(ExecutionException ex) {
			this.value = null;
			this.ex = ex;
		}
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return true;
		}

		@Override
		public T get() throws InterruptedException, ExecutionException {
			if (ex != null) {
				throw ex;
			}
			return value;
		}

		@Override
		public T get(long timeout, TimeUnit unit) throws InterruptedException,
				ExecutionException, TimeoutException {
			if (ex != null) {
				throw ex;
			}
			return value;
		}
		
	}

	static class TestValueFetcher implements ValueFetcher {
		private final int id;
		private int count = 0;
		public TestValueFetcher(int id) {
			this.id = id;
		}
		@Override
		public Map<Object, Object> call() throws Exception {
			Map<Object, Object> map =
					new HashMap<Object, Object>();
			map.put("a", id + count);
			map.put("b", 10 + id + count);
			count += 1;
			return map;
		}
		
	}
}
