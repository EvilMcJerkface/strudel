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
package com.nec.strudel.bench.micro.populate.base;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.params.DataParam;
import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.ValidateReporter;

public abstract class AbstractPopulatePost<T> implements Populator<T, ContentSet> {

	@Override
	public String getName() {
		return "Post";
	}

	@Override
	public ContentSet createParameter(PopulateParam param) {
		int userId = param.getId();
		int length = param.getInt(DataParam.CONTENT_LENGTH);
		RandomSelector<String> selector =
				RandomSelector.createAlphaString(length);
		return ContentSet.create(userId,
				param.getInt(DataParam.POSTS_PER_USER),
				selector, param.getRandom());
	}
	protected abstract List<Post> getPostsByUser(T db, int userId);

	@Override
	public boolean validate(T db,
			ContentSet param, ValidateReporter reporter) {
		int userId = param.getGroupId();
		List<Post> posts = getPostsByUser(db, userId);
		return validate(param, posts, reporter);
	}

	protected boolean validate(ContentSet param, List<Post> posts,
			ValidateReporter reporter) {
		String[] contents = param.getContents();
		Set<String> contentSet =
				new HashSet<String>();
		for (int i = 0; i < contents.length; i++) {
			contentSet.add(contents[i]);
		}
		if (posts.size() != contents.length) {
			reporter.error(contents.length + " posts expected but "
					+ posts.size() + " found");
			return false;
		}
		for (Post post : posts) {
			if (!contentSet.contains(post.getContent())) {
				reporter.error("invalid content in a post("
						+ post.getItemId() + ")");
				return false;
			}
		}
		return true;
		
	}

}