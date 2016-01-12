package com.nec.strudel.workload.session.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.nec.strudel.instrument.InstrumentUtil;
import com.nec.strudel.instrument.impl.ProfilerServiceImpl;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;
import com.nec.strudel.target.Target;
import com.nec.strudel.target.TargetUtil;
import com.nec.strudel.workload.session.SessionContainer;
import com.nec.strudel.workload.session.SessionProfiler;
import com.nec.strudel.workload.session.SessionProfilerImpl;
import com.nec.strudel.workload.session.runner.event.ActionResult;
import com.nec.strudel.workload.session.runner.event.SessionContext;
import com.nec.strudel.workload.session.runner.event.SessionEvent;
import com.nec.strudel.workload.session.test.tool.IncrementCount;
import com.nec.strudel.workload.session.test.tool.RepeatSession;

public class SessionEventTest {

	@Test
	public void test() {
		AtomicInteger counter = new AtomicInteger();
		Target<AtomicInteger> target = TargetUtil.sharedTarget(counter);
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
		ProfilerServiceImpl profs = ProfilerServiceImpl.noService();
		SessionContainer<AtomicInteger> sc =
				new SessionContainer<AtomicInteger>(session, state);
		SessionProfiler prof = SessionProfilerImpl.noProfile();
		SessionContext<AtomicInteger> ctxt =
				new SessionContext<AtomicInteger>(target, target.open(profs), 
						InstrumentUtil.uninstrumented(prof));
		SessionEvent<AtomicInteger> event = new SessionEvent<AtomicInteger>(sc,
				ctxt);

		for (int i = 0; i < repeatCount; i++) {
			ActionResult<AtomicInteger> res = event.call();
			Result r = res.getResult();
			assertTrue(r.isSuccess());
			int count = r.get(IncrementCount.TestParam.COUNT);
			assertEquals(i + 1, count);
			assertEquals(i + 1, counter.get());
			if (sc.isActive()) {
				event = res.nextAction();
				assertNotNull(event);
			} else {
				assertNull(res.nextAction());
			}
		}
	}
}
