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
package com.nec.strudel.workload.jobexec.test.populate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.nec.strudel.param.ConstantParamSeq;
import com.nec.strudel.param.ParamConfig;
import com.nec.strudel.target.TargetUtil;
import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;
import com.nec.strudel.tkvs.Transaction;
import com.nec.strudel.tkvs.TransactionalDB;
import com.nec.strudel.tkvs.store.impl.InMemoryStore;
import com.nec.strudel.workload.job.PopulateTask;
import com.nec.strudel.workload.job.PopulateWorkItem;
import com.nec.strudel.workload.jobexec.PopulateRunner;
import com.nec.strudel.workload.jobexec.test.LocalPopulateExec;

public class TKVStorePopulateTest {
	static final AtomicInteger COUNTER = new AtomicInteger();
	static final InMemoryStore STORE = new InMemoryStore();
	private Properties props = new Properties();


	public TransactionalDB getDB(String name) {
		return STORE.create(name, props).open();
	}
	protected String newName() {
		return "db" + COUNTER.getAndIncrement();
	}

    @Test
    public void testPopulate() {
        final int numOfRecords = 100;
        final String collection = TKVTestPopulatorFactory.COLLECTION_NAME;
        String dbName = newName();
        TransactionalDB db = getDB(dbName);
        populator(db, numOfRecords).run();
        for (int i = 0; i < numOfRecords; i++) {
            Key key = Key.create(i);
            Transaction tx = db.start(collection, key);
            Record r = tx.get(collection, key);
            assertTrue(tx.commit());
            assertNotNull(r);
            assertEquals(TKVTestPopulatorFactory.recordOf(i), r);
        }
        {
            Key key = Key.create(numOfRecords);
            Transaction tx = db.start(collection, key);
            Record r = tx.get(collection, key);
            assertTrue(tx.commit());
            assertNull(r);
        }
    }
	private PopulateRunner populator(TransactionalDB db, int numOfRecords) {
		ConstantParamSeq pseq = ConstantParamSeq.builder()
        		.param(TKVTestPopulatorFactory.InParam.COUNT.name(), numOfRecords)
        		.build();
        ParamConfig param = toParam(
        		pseq.nextParam(new Random()));

        PopulateTask pop = new PopulateTask();
        pop.setNumOfThreads(1);
        pop.setFactory(TKVTestPopulatorFactory.class.getName());
        pop.setValidate(true);
        PopulateWorkItem item = new PopulateWorkItem();
        item.setName(TKVTestPopulatorFactory.TASK_NAME);
        item.setMin(0);
        item.setMax(1);
        item.setParams(param);
        pop.addItem(item);
        PopulateRunner runner =
                PopulateRunner.create(pop, null,
                		 new LocalPopulateExec<TransactionalDB>(
                         		/**
                         		 * OK for a single thread execution:
                         		 */
                				 TargetUtil.sharedTarget(db)));
        return runner;
	}

	public static ParamConfig toParam(Map<String, Object> values) {
		ParamConfig pc = new ParamConfig();
		for (Map.Entry<String, Object> e
				: values.entrySet()) {
			pc.put(e.getKey(), e.getValue().toString());
		}
		return pc;
	}

}
