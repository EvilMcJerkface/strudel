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
