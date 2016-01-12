package com.nec.strudel.workload.session.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;
import com.nec.strudel.workload.session.SessionContainer;
import com.nec.strudel.workload.session.SessionProfiler;
import com.nec.strudel.workload.session.SessionProfilerImpl;
import com.nec.strudel.workload.session.test.tool.IncrementCount;
import com.nec.strudel.workload.session.test.tool.RepeatSession;


public class SessionContainerTest {

	@Test
	public void test() {
		int repeatCount = 10;
		int prepareTime = 100;
		int thinkTime = 200;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(IncrementCount.TestParam.COUNT.name(), 0);
		State state = State.newState(param, new Random());
		RepeatSession<AtomicInteger> session = new RepeatSession<AtomicInteger>("test",
				new IncrementCount(), repeatCount);
		session.setPrepareTime(prepareTime);
		session.setThinkTime(thinkTime);

		SessionContainer<AtomicInteger> sc =
				new SessionContainer<AtomicInteger>(session, state);
		SessionProfiler prof = SessionProfilerImpl.noProfile();

		assertEquals(prepareTime, sc.waitTime());
		AtomicInteger counter = new AtomicInteger();
		for (int i = 0; i < repeatCount; i++) {
			assertTrue(sc.isActive());
			Result res = sc.doAction(prof, counter);
			assertTrue(res.isSuccess());
			int count = res.get(IncrementCount.TestParam.COUNT);
			assertEquals(i + 1, count);
			assertEquals(i + 1, counter.get());
			if (sc.isActive()) {
				assertEquals(thinkTime + prepareTime, sc.waitTime());
			}
		}
		assertFalse(sc.isActive());
	}

}
