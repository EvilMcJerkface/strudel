/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nec.strudel.bench.micro.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.interactions.base.AbstractGetMyPost.InParam;
import com.nec.strudel.bench.micro.interactions.entity.GetMyPost;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.bench.test.EntityAssert;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(GetMyPost.class)
public class GetMyPostTest extends AbstractInteractionTestBase {
	/**
	 * Unlike GetPost, the current user (USER_ID) is
	 * chosen for the owner of a post.
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
    	assertEquals(userId, id.getUserId());
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

		EntityAssert.assertEntityEquals(item,
				res.get(TransitionParam.POST));
		EntityAssert.assertEntityEquals(item,
				complete(res).get(TransitionParam.POST));


	}
	@Test
	public void testExecuteEmpty() {
		final int userId = 4;
		final int itemNo = 2;

		Result res = executor()
				.param(InParam.ITEM_ID, new ItemId(userId, itemNo))
				.executeSuccess();

		assertEquals(ResultMode.EMPTY_RESULT, res.getMode());
		assertNull(res.get(TransitionParam.POST));
		assertNull(complete(res).get(TransitionParam.POST));
	}

}
