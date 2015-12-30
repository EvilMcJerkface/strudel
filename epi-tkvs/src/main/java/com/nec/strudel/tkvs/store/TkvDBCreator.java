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
