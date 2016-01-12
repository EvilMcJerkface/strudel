package com.nec.strudel.workload.session.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.nec.strudel.instrument.InstrumentUtil;
import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.impl.ProfilerServiceImpl;
import com.nec.strudel.target.Target;
import com.nec.strudel.target.TargetUtil;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.session.SessionProfiler;
import com.nec.strudel.workload.session.SessionProfilerImpl;
import com.nec.strudel.workload.session.StateFactory;
import com.nec.strudel.workload.session.runner.SessionProfilerServer;
import com.nec.strudel.workload.session.runner.event.ActionResult;
import com.nec.strudel.workload.session.runner.event.SessionEventSeq;
import com.nec.strudel.workload.session.test.tool.FixedLengthParamSequence;
import com.nec.strudel.workload.session.test.tool.IncrementCount;
import com.nec.strudel.workload.session.test.tool.RepeatSession;
import com.nec.strudel.workload.exec.event.TimedEvent;

public class SessionEventSeqTest {

	@Test
	public void test() throws Exception {
		AtomicInteger counter = new AtomicInteger();
		Target<AtomicInteger> target = TargetUtil.sharedTarget(counter);
		int repeatCount = 10;
		int sessionCount = 100;
		int sessionConcurrency = 10;
		int prepareTime = 100;
		int thinkTime = 200;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(IncrementCount.TestParam.COUNT.name(), 0);
		Random rand = new Random();
		StateFactory states = new StateFactory(
				new FixedLengthParamSequence(sessionCount, param), rand);
		SessionFactory<AtomicInteger> sfactory =
				new RepeatSession<AtomicInteger>("test", 
				new IncrementCount(), repeatCount)
				.setPrepareTime(prepareTime)
				.setThinkTime(thinkTime)
				.toFactory();
		ProfilerServiceImpl profs = ProfilerServiceImpl.noService();
		SessionEventSeq<AtomicInteger> seq = new SessionEventSeq<AtomicInteger>(sfactory, states, 
				sessionConcurrency, target, new NoSessionProfilerServer(),
				profs, rand);
		execute(seq);

		assertEquals(sessionCount * repeatCount, counter.get());
	}

	void execute(SessionEventSeq<AtomicInteger> seq) throws Exception {
		LinkedList<TimedEvent<ActionResult<AtomicInteger>>> queue =
				new LinkedList<TimedEvent<ActionResult<AtomicInteger>>>();
		queue.addAll(seq.start());
		do {
			TimedEvent<ActionResult<AtomicInteger>> event = queue.removeFirst();
			ActionResult<AtomicInteger> res = event.call();
			queue.addAll(seq.next(res));
		} while (!queue.isEmpty());

		
	}
	static class NoSessionProfilerServer implements SessionProfilerServer {

		@Override
		public Instrumented<? extends SessionProfiler> profiler() {
			return InstrumentUtil.uninstrumented(SessionProfilerImpl.noProfile());
		}
		
	}
}
