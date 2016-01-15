package com.nec.strudel.metrics;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReportTest {


	@Test
	public void testAggregateNone() {
		Report rep = Report.aggregate(Report.none(),
				Report.none());
		assertNotNull(rep.getValues());
		assertTrue(rep.getWarns().isEmpty());
	}

	@Test
	public void testTransferNone() {
		Report r = Report.none();
		Report r1 = Report.toReport(r.toJson());
		assertNotNull(r1.getValues());
		assertTrue(r1.getValues().keySet().isEmpty());
		assertTrue(r1.getWarns().isEmpty());
	}
}
