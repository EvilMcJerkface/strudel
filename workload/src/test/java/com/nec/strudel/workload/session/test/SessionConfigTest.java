package com.nec.strudel.workload.session.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.nec.strudel.param.ParamSequence;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.impl.State;
import com.nec.strudel.workload.session.MarkovSession;
import com.nec.strudel.workload.session.RandomSession;
import com.nec.strudel.workload.session.Session;
import com.nec.strudel.workload.session.SessionConfig;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.session.StateFactory;
import com.nec.strudel.workload.session.UserAction;
import com.nec.strudel.workload.session.test.tool.Factory;
import com.nec.strudel.workload.test.Resources;
import com.nec.strudel.workload.test.SessionFiles;

public class SessionConfigTest {
	private Random rand = new Random();

	@Test
	public void testRandomSession() {
		Set<String> intractionNames = new HashSet<String>(
				Arrays.asList("I0", "I1", "I2", "I3"));

		@SuppressWarnings("unchecked")
		SessionConfig<Object> conf = Resources.create(
				SessionFiles.SESSION001);
		SessionFactory<Object> sf = conf.createSessionFactory("");
		StateFactory stf = createStateFactories(conf, 0, 1, 1, rand).get(0);
		for (int s = 0; s < 5; s++) {
			Session<Object> session = sf.create();
			assertTrue(session instanceof RandomSession);
			State state = stf.next();
			Map<String, Object> param = new HashMap<String, Object>();
			for (Map.Entry<String, Object> e : state) {
				param.put(e.getKey(), e.getValue());
			}
			for (int i = 1; i <= 3; i++) {
				assertEquals("v" + i, param.get("k" + i));
			}
			for (int i = 0; i < 10; i++) {
				UserAction<Object> action = session.next(state);
				Interaction<Object> intr = action.getInteraction();
				assertTrue(intr instanceof Factory.TestIntr);
				assertTrue(intractionNames.contains(action.getName()));
				long before = action.getPrepareTime();
				long after = action.getThinkTime();
				if (action.getName().equals("I0")) {
					assertEquals(5000, before);
					assertEquals(2000, after);
				} else {
					assertEquals(3000, before);
					assertTrue(after <= 40000);
				}
			}
			
		}
	}
	@Test
	public void testMarkovSession() {
		@SuppressWarnings("unchecked")
		SessionConfig<Object> conf = Resources.create(
				SessionFiles.SESSION002);
		SessionFactory<Object> sf = conf.createSessionFactory("");
		StateFactory stf = createStateFactories(conf, 0, 1, 1, rand).get(0);
		for (int s = 0; s < 5; s++) {
			Session<Object> session = sf.create();
			assertTrue(session instanceof MarkovSession);
			State state = stf.next();

			Map<String, Object> param = new HashMap<String, Object>();
			for (Map.Entry<String, Object> e : state) {
				param.put(e.getKey(), e.getValue());
			}
			for (int i = 1; i <= 3; i++) {
				assertEquals("v" + i, param.get("k" + i));
			}
			String prev = "START";
			for (int i = 0; i < 20; i++) {
				UserAction<Object> action = session.next(state);
				if ("I3".equals(prev)) {
					assertNull(action);
					break;
				}
				Interaction<Object> intr = action.getInteraction();
				assertTrue(intr instanceof Factory.TestIntr);
				String name = action.getName();
				if ("START".equals(prev)) {
					assertEither(name, "I0", "I1");					
				} else if ("I0".equals(prev)) {
					assertEither(name, "I0", "I1");
				} else if ("I1".equals(prev)) {
					assertEither(name, "I0", "I1", "I2");
				} else if ("I2".equals(prev)) {
					assertEither(name, "I0", "I1", "I3");
				} else {
					fail("unknown state: " + name);
				}
				long before = action.getPrepareTime();
				long after = action.getThinkTime();
				assertEquals(0, before);
				assertEquals(0, after);
				prev = name;
			}
			
		}
	}
	private void assertEither(String name, String...expected) {
		HashSet<String> set = new HashSet<String>(Arrays.asList(expected));
		assertTrue(name + " must be in " + set,
				set.contains(name));
	}
	/**
	 * Generates a list of state factories that are used by execution
	 * threads of a node.
	 * @param nid the node ID in the set of the nodes
	 * (0,1,2,..)
	 * @param nodes the size of the set of the nodes.
	 * @param threads the number of threads per node.
	 * @param rand a random to generate a random seed for
	 * each state factory.
	 */
	public List<StateFactory> createStateFactories(SessionConfig<?> conf, int nid, int nodes,
			int threads, Random rand) {
		List<StateFactory> list = new ArrayList<StateFactory>();
		ParamSequence[] seqs = conf.getParams()
				.createParamSeqVector(nid, nodes, threads);
		for (int i = 0; i < threads; i++) {
			list.add(new StateFactory(
	               seqs[i],
	               new Random(rand.nextLong())));
		}
		return list;
	}
}
