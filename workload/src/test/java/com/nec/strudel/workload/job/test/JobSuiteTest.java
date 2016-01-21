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
package com.nec.strudel.workload.job.test;

import static org.junit.Assert.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.nec.congenio.ConfigValue;
import com.nec.strudel.workload.job.Job;
import com.nec.strudel.workload.job.JobSuite;
import com.nec.strudel.workload.test.ResourceNames;
import com.nec.strudel.workload.test.Resources;

public class JobSuiteTest {

	@Test
	public void testJobSuite() {
		int[] ws = {1,2,3,4};
		int[] as = {1,2};
		int[] bs = {10, 20};
	    File file = Resources.getFile(ResourceNames.JOB_SUITE1);
    	SimpleDateFormat df =
    			new SimpleDateFormat("yyyy-MM-dd-HHmmss");
    	String begin = df.format(new Date());
	    JobSuite js = JobSuite.create(file);
	    String end = df.format(new Date());

	    assertTrue(begin.compareTo(js.getId()) <= 0);
	    assertTrue(end.compareTo(js.getId()) >= 0);

	    Set<Integer> wSet = new HashSet<Integer>();
	    int count = 0;
	    for (Job job : js) {
	    	count++;
	    	ConfigValue vset = job.getConfig();
	    	wSet.add(vset.getInt("w"));
	    	int a = vset.getInt("a");
	    	assertEquals(a, vset.getInt("a1"));
	    	int b = vset.getInt("b");
	    	boolean found = false;
	    	for (int i = 0; i < as.length; i++) {
	    		if (a == as[i]) {
	    			assertEquals(bs[i], b);
	    			found = true;
	    			break;
	    		}
	    	}
	    	assertTrue(found);
	    	assertEquals(100, vset.getInt("x"));
	    	assertEquals(200, vset.getInt("y"));
	    	assertEquals(300, vset.getInt("z"));
	    }
	    assertEquals(ws.length * as.length, count);
	    assertEquals(ws.length, wSet.size());
	    for (int w : ws) {
	    	assertTrue(wSet.contains(w));
	    }
	    assertEquals("dir", js.getOutput());

	}

}
