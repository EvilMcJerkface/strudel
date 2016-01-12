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

import javax.json.Json;
import javax.json.JsonValue;

import org.junit.Test;

import com.nec.congenio.json.JsonValueUtil;
import com.nec.strudel.workload.measure.ClusterAggregation;

public class ClusterAggregationTest {


	@Test
	public void test() {
		ClusterAggregation[] aggrs = {
				ClusterAggregation.get("sum"),
				ClusterAggregation.get("avg"),
				ClusterAggregation.get("array"),
		};
		JsonValue[] truth = {
			JsonValueUtil.create(16),	
			JsonValueUtil.create(4.0),
			Json.createArrayBuilder()
			.add(2).add(3).add(5).add(6)
			.build()
		};
		Object[] jsonvals = {
				JsonValueUtil.create(2),
				JsonValueUtil.create(3),
				JsonValueUtil.create(5),
				JsonValueUtil.create(6),
		};
		Object[] numvals = {
			2, 3, 5, 6	
		};
		for (int i = 0; i < aggrs.length; i++) {
			ClusterAggregation aggr = aggrs[i];
			assertEquals(truth[i],
					aggr.aggregate(jsonvals));
			assertEquals(truth[i],
					aggr.aggregate(numvals));
		}
	}
}
