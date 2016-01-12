package com.nec.strudel.workload.param.test;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.nec.strudel.param.ParamConfig;
import com.nec.strudel.param.ParamSequence;
import com.nec.strudel.workload.test.ParamFiles;
import com.nec.strudel.workload.test.Resources;

public class ParamSequenceTest {


	@Test
	public void testXMLParamSequence() {
		/**
		 * n1 = "v1", n2 = "v2", n3 = int(0,3), n4 = double(0,2)
		 */
		ParamConfig pxml = Resources.create(ParamFiles.PARAM001);
		ParamSequence pseq = pxml.createParamSeq();
		Random rand = new Random();
		for (int i = 0; i < 10; i++) {
			Map<String, Object> param = pseq.nextParam(rand);
			assertEquals(4, param.size());
			assertEquals("v1", param.get("n1"));
			assertEquals("v2", param.get("n2"));
			Object v3 = param.get("n3");
			assertTrue(v3 instanceof Integer);
			int i3 = (Integer) v3;
			assertTrue(0 <= i3 && i3 < 3);
			Object v4 = param.get("n4");
			assertTrue(v4 instanceof Double);
			double d4 = (Double) v4;
			assertTrue(0 <= d4 && d4 < 2);
		}
	}
	@Test
	public void testParamSequenceViaConfig() {
		/**
		 * n1 = "v1", n2 = "v2", n3 = int(0,3), n4 = double(0,2)
		 */
		ParamConfig pxml = Resources.create(ParamFiles.PARAM001);

		/**
		 * convert to ConfigValue and back.
		 */
		pxml = pxml.getConfig().toObject(ParamConfig.class);

		ParamSequence pseq = pxml.createParamSeq();
		Random rand = new Random();
		for (int i = 0; i < 10; i++) {
			Map<String, Object> param = pseq.nextParam(rand);
			assertEquals(4, param.size());
			assertEquals("v1", param.get("n1"));
			assertEquals("v2", param.get("n2"));
			Object v3 = param.get("n3");
			assertTrue(v3 instanceof Integer);
			int i3 = (Integer) v3;
			assertTrue(0 <= i3 && i3 < 3);
			Object v4 = param.get("n4");
			assertTrue(v4 instanceof Double);
			double d4 = (Double) v4;
			assertTrue(0 <= d4 && d4 < 2);
		}
	}
	@Test
	public void testParamGenerator() {
		/**
		 * n1 = "v1", n2 = "v2", n3 = int(0,100), n4 = partitioned(int(0,100))
		 */
		ParamConfig pxml = Resources.create(ParamFiles.PARAM002);
		int threads = 10;
		Random rand = new Random();
		ParamSequence[] pseqs = pxml.createParamSeqVector(0, 1, threads);
		for (int i = 0; i < threads; i++) {
			ParamSequence pseq = pseqs[i];
			for (int j = 0; j < 10; j++) {
				Map<String, Object> param = pseq.nextParam(rand);
				assertEquals(4, param.size());
				assertEquals("v1", param.get("n1"));
				assertEquals("v2", param.get("n2"));
				Object v3 = param.get("n3");
				assertTrue(v3 instanceof Integer);
				int i3 = (Integer) v3;
				assertTrue(0 <= i3 && i3 < 100);
				Object v4 = param.get("n4");
				assertTrue(v4 instanceof Integer);
				int i4 = (Integer) v4;
				int min = i * 10;
				int max = min + 10;
				assertTrue(min <= i4 && i4 < max);
				
			}
		}
	}
}
