package com.nec.strudel.bench.test.populate;

import org.junit.Assert;

import com.nec.strudel.bench.test.AbstractEntityTestBase;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.util.ClassUtil;
import com.nec.strudel.workload.api.Populator;

public abstract class AbstractPopulateTestBase<T>
extends AbstractEntityTestBase {

	@SuppressWarnings("unchecked")
	protected Populator<EntityDB, T> populator() {
    	TestOn testOn = this.getClass().getAnnotation(TestOn.class);
    	if (testOn == null) {
    		Assert.fail("@TestOn not found in " + this.getClass());
    	}
    	return (Populator<EntityDB, T>) ClassUtil.create(testOn.value());
    }

	public void process(T param) {
    	Populator<EntityDB, T> pop = populator();
    	EntityDB db = getDb();
    	PopulateUtil.process(pop, db, param);
	}
	public T process(PopulateUtil.ParamBuilder param) {
    	Populator<EntityDB, T> pop = populator();
    	T p = pop.createParameter(param.build());
    	EntityDB db = getDb();
    	PopulateUtil.process(pop, db, p);
    	return (T) p;
    }
	protected static PopulateUtil.ParamBuilder param(int id) {
        return new PopulateUtil.ParamBuilder(id);
    }
}
