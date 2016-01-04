package com.nec.strudel.bench.micro.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.interactions.base.AbstractGetPost.InParam;
import com.nec.strudel.bench.micro.interactions.base.AbstractGetPost.OutParam;
import com.nec.strudel.bench.micro.interactions.entity.GetPost;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.test.EntityAssert;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(GetPost.class)
public class GetPostTest extends AbstractInteractionTestBase {

	/**
	 * ITEM_ID is chosen
	 * for a random user from the items
	 * per user.
	 */
	@Test
	public void testPrepare() {
		final int userId = 2;
		final int userNum = 3;
		final int minUid = 1;
		final int minSeqNo = 1;
		final int postsPerUser = 2;
		State state = newState()
		.put(SessionParam.USER_ID, userId)
		.put(SessionParam.MIN_USER_ID, minUid)
		.put(SessionParam.USER_NUM, userNum)
		.put(SessionParam.MIN_SEQ_NO, minSeqNo)
		.put(SessionParam.POSTS_PER_USER, postsPerUser);
    	Param param = prepare(state);
    	ItemId id = param.getObject(InParam.ITEM_ID);
    	assertNotNull(id);
    	assertNotSame(userId, id.getUserId());
    	assertTrue(minUid <= id.getUserId());
    	assertTrue(id.getUserId() < minUid + userNum);
    	assertTrue(minSeqNo <= id.getItemNo());
    	assertTrue(id.getItemNo() < minSeqNo + postsPerUser);
	}
	@Test
	public void testExecute() {
		final int userId = 14;
		Post item = populateNew(Post.class,
				new ContentBuilder<Post>("userId", userId));

		Result res = executor()
				.param(InParam.ITEM_ID, item.getItemId())
				.executeSuccess();

		EntityAssert.assertEntityEquals(item, res.get(OutParam.POST));
	}
	@Test
	public void testExecuteEmpty() {
		final int userId = 4;
		final int itemNo = 2;

		Result res = executor()
				.param(InParam.ITEM_ID, new ItemId(userId, itemNo))
				.executeSuccess();

		assertEquals(ResultMode.EMPTY_RESULT, res.getMode());
		assertNull(res.get(OutParam.POST));
	}


}
