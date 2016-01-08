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
