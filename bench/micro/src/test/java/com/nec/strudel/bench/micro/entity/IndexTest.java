package com.nec.strudel.bench.micro.entity;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.PairId;
import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.entity.IndexType;

public class IndexTest {

	@Test
	public void testSubmitPairIndex() {
		IndexType type = IndexType.on(Submission.class, "pairId");
		Submission sub = new Submission();
		sub.setReceiverId(1);
		sub.setSenderId(2);
		sub.setSubmitNo(1);
		sub.setContent("test");
		Object idxKey = type.getIndexKey(sub);
		PairId pid = (PairId) idxKey;
		assertEquals(sub.getReceiverId(), pid.getReceiverId());
		assertEquals(sub.getSenderId(), pid.getSenderId());
		assertEquals(sub.getReceiverId(), type.toGroupKey(pid));
	}
}
