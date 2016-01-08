/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
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
package com.nec.strudel.tkvs.store;

import com.nec.strudel.target.Target;
import com.nec.strudel.target.TargetConfig;
import com.nec.strudel.target.TargetCreator;
import com.nec.strudel.target.TargetLifecycle;
import com.nec.strudel.tkvs.TransactionalDB;
import com.nec.strudel.util.ClassUtil;

@SuppressWarnings("rawtypes")
public class TkvDBCreator implements TargetCreator {

	@Override
	public Target create(TargetConfig dbConfig) {
		TransactionalStore s = createStore(dbConfig);
		Target<TransactionalDB> tkvStore =
				s.create(dbConfig.getName(),
				dbConfig.getProperties());
		String type = dbConfig.getType();
		if ("tkvs".equals(type)) {
			return tkvStore;
		} else {
			return new EntityStore(tkvStore);
		}
	}
	@Override
	public TargetLifecycle createLifecycle(TargetConfig dbConfig) {
		TransactionalStore s = createStore(dbConfig);
		return s.lifecycle(dbConfig.getName(),
				dbConfig.getProperties());
	}

	@Override
	public Class<?> instrumentedClass(TargetConfig conf) {
		return ClassUtil.forName(
				conf.getClassName(), conf.targetClassLoader());
	}

	private TransactionalStore createStore(TargetConfig conf) {
		return ClassUtil.create(conf.getClassName(),
				conf.targetClassLoader());
	}

}
