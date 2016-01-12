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
