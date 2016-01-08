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
package com.nec.strudel.bench.micro.populate;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.params.DataParam;
import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.bench.micro.populate.entity.PopulatePost;
import com.nec.strudel.bench.test.populate.AbstractPopulateTestBase;
import com.nec.strudel.bench.test.populate.TestOn;

@TestOn(PopulatePost.class)
public class PopulatePostTest extends AbstractPopulateTestBase<ContentSet> {

	@Test
	public void test() {
		final int uid = 13;
		final int contentLength = 11;
		final int postsPerUser = 6;
		ContentSet cset = process(param(uid)
				.param(DataParam.CONTENT_LENGTH, contentLength)
				.param(DataParam.POSTS_PER_USER, postsPerUser));
		assertEquals(uid, cset.getGroupId());
		assertEquals(postsPerUser, cset.getContents().length);
		for (String c : cset.getContents()) {
			assertEquals(contentLength, c.length());
		}
		List<Post> posts = getList(Post.class, "userId", uid);
		assertEquals(postsPerUser, posts.size());
	}
}
