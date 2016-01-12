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
package com.nec.strudel.workload.jobexec.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nec.strudel.target.Target;
import com.nec.strudel.target.impl.DatabaseConfig;
import com.nec.strudel.target.impl.TargetFactory;
import com.nec.strudel.workload.job.PopulateTask;
import com.nec.strudel.workload.jobexec.PopulateRunner;
import com.nec.strudel.workload.test.DBFiles;
import com.nec.strudel.workload.test.PopulateFiles;
import com.nec.strudel.workload.test.Resources;
import com.nec.strudel.workload.test.kvmap.KVMap;

public class PopulateRunnerTest {


    @Test
    public void populateTest() {
    	PopulateTask popTask = Resources.create(PopulateFiles.POPULATE001);
    	DatabaseConfig dbConf = Resources.create(DBFiles.DB_TEST);
    	Target<KVMap> store = TargetFactory.create(dbConf);
    	PopulateRunner pop = PopulateRunner.create(popTask, dbConf,
    			new LocalPopulateExec<KVMap>(store));
    	pop.run();
    	KVMap map = store.open();
    	int min = 50;
    	int max = 150;
    	for (int i = min; i < max; i++) {
    		assertTrue("x" + i + " should exist",
    				map.hasValue("x" + i));
    		int x = map.get("x" + i);
    		assertEquals("x" + i, 100, x);
    	}
    	assertFalse(map.hasValue("x" + max));
    	int size = 100;
    	for (int i = 0; i < size; i++) {
    		assertTrue("y" + i + " should exist",
    				map.hasValue("y" + i));
    	}
    	assertFalse(map.hasValue("y" + size));
    }
}
