package com.nec.strudel.workload.test.kvmap;

import com.nec.strudel.target.Target;
import com.nec.strudel.target.TargetConfig;
import com.nec.strudel.target.TargetCreator;
import com.nec.strudel.target.TargetLifecycle;
import com.nec.strudel.target.TargetUtil;

public class KVMapCreator implements TargetCreator<KVMap> {

	@Override
	public Target<KVMap> create(TargetConfig dbConfig) {
		KVMap map = new KVMap(dbConfig.getName());
		return TargetUtil.sharedTarget(map);
	}

	@Override
	public TargetLifecycle createLifecycle(TargetConfig dbConfig) {
		return new TargetLifecycle() {
			
			@Override
			public void close() {
			}
			
			@Override
			public void operate(String name) {
			}
		};
	}
	@Override
	public Class<?> instrumentedClass(TargetConfig config) {
		return null;
	}
}
