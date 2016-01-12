package com.nec.strudel.workload.job.test;

import static org.junit.Assert.*;

import java.io.File;
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
	    JobSuite js = JobSuite.create(file);
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
	    String dir = js.get("dir");
	    assertEquals(dir, js.getOutput());

	}
}
