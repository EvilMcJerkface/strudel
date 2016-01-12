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
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.junit.Test;

import com.nec.congenio.json.JsonValueUtil;
import com.nec.strudel.workload.measure.ResultAggregation;
import com.nec.strudel.workload.measure.ResultAggregationFactory;

public class ResultAggregationFactoryTest {


	@Test
	public void testAvg() {
		ResultAggregation aggr = new ResultAggregationFactory(
				ResultAggregationFactory.TYPE_AVG)
					.create();
		for (int t = 0; t < 2; t++) {
			aggr.clear();
			for (int i = 0; i < 5; i++) {
				JsonObject value = Json.createObjectBuilder()
						.add("a", i + t)
						.add("b", i + t + 10)
						.build();
				aggr.put(value);
			}
			JsonValue v = aggr.get();
			assertTrue(v instanceof JsonObject);
			JsonObject vmap = (JsonObject) v;
			assertEquals(2, vmap.size());
			assertEquals(JsonValueUtil.create(2.0 + t),vmap.get("a"));
			assertEquals(JsonValueUtil.create(12.0 + t),vmap.get("b"));
		}
	}

}
