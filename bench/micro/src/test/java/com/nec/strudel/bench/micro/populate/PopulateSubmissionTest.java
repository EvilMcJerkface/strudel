package com.nec.strudel.bench.micro.populate;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.params.DataParam;
import com.nec.strudel.bench.micro.populate.SubmitSet;
import com.nec.strudel.bench.micro.populate.entity.PopulateSubmission;
import com.nec.strudel.bench.test.populate.AbstractPopulateTestBase;
import com.nec.strudel.bench.test.populate.TestOn;

@TestOn(PopulateSubmission.class)
public class PopulateSubmissionTest extends AbstractPopulateTestBase<SubmitSet> {
	@Test
	public void test() {
		final int uid = 10;
		final int contentLength = 10;
		final int itemsPerUser = 4;
		final int minUid = 1;
		final int userNum = 15;
		SubmitSet sset = process(param(uid)
				.param(DataParam.CONTENT_LENGTH, contentLength)
				.param(DataParam.SUBMISSIONS_PER_USER, itemsPerUser)
				.param(DataParam.MIN_USER_ID, minUid)
				.param(DataParam.USER_NUM, userNum));
		assertEquals(itemsPerUser, sset.size());
		List<Submission> subs = getList(Submission.class, "senderId", uid);
		assertEquals(itemsPerUser, subs.size());
		for (Submission s : subs) {
			assertEquals(contentLength, s.getContent().length());
			assertTrue(minUid <= s.getReceiverId());
			assertTrue(s.getReceiverId() < minUid + userNum);
			assertIndexed(s, "receiverId");
			assertIndexed(s, "pairId");
		}
	}
}
