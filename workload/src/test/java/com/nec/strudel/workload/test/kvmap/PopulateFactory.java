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
package com.nec.strudel.workload.test.kvmap;

import java.util.HashMap;
import java.util.Map;

import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.PopulatorFactory;
import com.nec.strudel.workload.api.ValidateReporter;

public class PopulateFactory implements PopulatorFactory<KVMap> {
	public enum InParam {
		VALUE,
	}
	private static final Map<String, Populator<KVMap, ?>> POPS =
		new HashMap<String, Populator<KVMap, ?>>();
	private static void def(Populator<KVMap, ?> pop) {
		POPS.put(pop.getName(), pop);
	}
	static {
		def(new PopulateX());
		def(new PopulateY());
	}
	@Override
	public Populator<KVMap, ?> create(String name) {
		return POPS.get(name);
	}
	static class KeyValue {
		String key;
		int value;
		KeyValue(String key, int value) {
			this.key = key;
			this.value = value;
		}
		public String getKey() {
			return key;
		}
		public int getValue() {
			return value;
		}
	}
	public static class PopulateX implements Populator<KVMap, KeyValue> {

		@Override
		public String getName() {
			return "PopulateX";
		}

		@Override
		public KeyValue createParameter(PopulateParam param) {
			int id = param.getId();
			int value = param.getInt(InParam.VALUE);
			return new KeyValue("x" + id, value);
		}
		@Override
		public void process(KVMap db, KeyValue kv) {
			db.put(kv.getKey(), kv.getValue());
		}
		@Override
		public boolean validate(KVMap db, KeyValue kv,
				ValidateReporter reporter) {
			int value = kv.getValue();
			int result = db.get(kv.getKey());
			if (value != result) {
				reporter.error(kv.getKey() + "=" + result
						+ " (expected:" + value);
				return false;
			}
			return true;
		}
	}
	public static class PopulateY implements Populator<KVMap, KeyValue> {

		@Override
		public String getName() {
			return "PopulateY";
		}

		@Override
		public KeyValue createParameter(PopulateParam param) {
			int id = param.getId();
			int value = param.getInt(InParam.VALUE);
			return new KeyValue("y" + id, value);
		}
		@Override
		public void process(KVMap db, KeyValue kv) {
			db.put(kv.getKey(), kv.getValue());
		}
		@Override
		public boolean validate(KVMap db, KeyValue kv,
				ValidateReporter reporter) {
			int value = kv.getValue();
			int result = db.get(kv.getKey());
			if (value != result) {
				reporter.error(kv.getKey() + "=" + result
						+ " (expected:" + value);
				return false;
			}
			return true;
		}
	}

}
