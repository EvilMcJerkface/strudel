package com.nec.strudel.tkvs.store;

import java.util.Properties;

import com.nec.strudel.target.FactoryClass;
import com.nec.strudel.target.Target;
import com.nec.strudel.target.TargetLifecycle;
import com.nec.strudel.tkvs.TransactionalDB;

@FactoryClass(TkvDBCreator.class)
public interface TransactionalStore {

	Target<TransactionalDB> create(String dbName, Properties props);

	TargetLifecycle lifecycle(String dbName, Properties props);

}
