package com.nec.strudel.bench.micro.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.interactions.base.AbstractCreatePost.InParam;
import com.nec.strudel.bench.micro.interactions.entity.CreatePost;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.impl.State;

@TestOn(CreatePost.class)
public class CreatePostTest extends AbstractInteractionTestBase {

	@Test
	public void testPrepare() {
		final int userId = 10;
		final int length = 20;
		State state = newState()
		.put(SessionParam.USER_ID, userId)
		.put(SessionParam.CONTENT_LENGTH, length);
    	Param param = prepare(state);
    	assertEquals(userId, param.getInt(SessionParam.USER_ID));
    	String content = param.get(InParam.CONTENT);
    	assertNotNull(content);
    	assertEquals(length, content.length());
	}

	@Test
	public void testExecute() {
		final int userId = 12;
		final String content = "aaaaa";

		executor()
		.param(SessionParam.USER_ID, userId)
		.param(InParam.CONTENT, content)
		.executeSuccess();

		Post item = getSingle(Post.class, "userId", userId);
		assertEquals(userId, item.getUserId());
		assertEquals(content, item.getContent());
	}

}
