package com.nec.strudel.bench.micro.interactions;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdatePost.InParam;
import com.nec.strudel.bench.micro.interactions.entity.UpdatePost;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.impl.State;

@TestOn(UpdatePost.class)
public class UpdatePostTest extends AbstractInteractionTestBase {
	@Test
	public void testPrepare() {
		final int userId = 1;
		final int length = 20;

		Post post = new Post(userId, 1);
		post.setContent("test");
		State state = newState()
		.put(SessionParam.USER_ID, userId)
		.put(SessionParam.CONTENT_LENGTH, length)
		.put(TransitionParam.POST, post);
    	Param param = prepare(state);
    	String content = param.get(InParam.CONTENT);
    	assertNotNull(content);
    	assertEquals(length, content.length());
    	ItemId iid1 = param.getObject(InParam.ITEM_ID);
    	assertEquals(post.getItemId(), iid1);
	}

	@Test
	public void testPrepareRandomChoice() {
		final int userId = 1;
		final int minSeqNo = 1;
		final int itemsPerUser = 5;
		final int length = 20;
		State state = newState()
		.put(SessionParam.USER_ID, userId)
		.put(SessionParam.MIN_SEQ_NO, minSeqNo)
		.put(SessionParam.POSTS_PER_USER, itemsPerUser)
		.put(SessionParam.CONTENT_LENGTH, length);
    	Param param = prepare(state);
    	String content = param.get(InParam.CONTENT);
    	assertNotNull(content);
    	assertEquals(length, content.length());
    	ItemId iid = param.getObject(InParam.ITEM_ID);
    	assertEquals(userId, iid.getUserId());
    	assertTrue(minSeqNo <= iid.getItemNo());
    	assertTrue(iid.getItemNo() < minSeqNo + itemsPerUser);
	}
	@Test
	public void testExecute() {
		final int userId = 3;
		final String originalContent = "original";
		final String updatedContent = "updated";

		Post post = populateNew(Post.class,
				new ContentBuilder<Post>("userId", userId)
				.prefix(originalContent));

		executor()
		.param(InParam.ITEM_ID, post.getItemId())
		.param(InParam.CONTENT, updatedContent)
		.executeSuccess();

		Post updated = getNotNull(Post.class, post.getItemId());
		assertEquals(updatedContent, updated.getContent());

	}
}
