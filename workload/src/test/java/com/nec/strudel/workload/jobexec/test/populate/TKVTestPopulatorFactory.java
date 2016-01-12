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

import java.util.HashMap;
import java.util.Map;

import com.nec.strudel.tkvs.Key;
import com.nec.strudel.tkvs.Record;
import com.nec.strudel.tkvs.Transaction;
import com.nec.strudel.tkvs.TransactionRunner;
import com.nec.strudel.tkvs.TransactionTask;
import com.nec.strudel.tkvs.TransactionalDB;
import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.PopulatorFactory;
import com.nec.strudel.workload.api.ValidateReporter;

/**
 * Populates the specified number N of records whose keys are 0, 1, 2,...N-1.
 * @author tatemura
 *
 */
public class TKVTestPopulatorFactory implements PopulatorFactory<TransactionalDB> {
	public enum InParam {
		COUNT,
	}
    public static final String TASK_NAME = "populate";
    public static final String COLLECTION_NAME = "r";
    private static final Map<String, Populator<TransactionalDB, ?>> POPS =
            new HashMap<String, Populator<TransactionalDB, ?>>();
    static {
        def(new PopulateRecords());
    }
    private static final void def(Populator<TransactionalDB, ?> task) {
        POPS.put(task.getName(), task);
    }
    @Override
    public Populator<TransactionalDB, ?> create(String name) {
        return POPS.get(name);
    }

    public static Record recordOf(int id) {
        return Record.create(id, "v" + id);
    }
    public static class PopulateRecords implements Populator<TransactionalDB, PopulateParam> {

        @Override
        public String getName() {
            return TASK_NAME;
        }
        @Override
        public PopulateParam createParameter(PopulateParam param) {
        	return param;
        }

        @Override
        public void process(TransactionalDB db, PopulateParam p) {
        	PopulateParam param = (PopulateParam) p;
            int count = param.getInt(InParam.COUNT);
            final String name = COLLECTION_NAME;
            for (int i = 0; i < count; i++) {
                final Key key = Key.create(i);
                final Record value = recordOf(i);
                TransactionRunner.run(db, name, key, new TransactionTask<Void>() {
                    @Override
                    public Void run(Transaction tx) {
                        tx.put(name, key, value);
                        return null;
                    }
                });
            }
        }
        @Override
        public boolean validate(TransactionalDB db, PopulateParam param,
        		ValidateReporter reporter) {
        	// TODO implement validation
        	return true;
        }
    }
}
