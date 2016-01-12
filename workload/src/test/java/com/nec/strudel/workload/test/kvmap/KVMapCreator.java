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
