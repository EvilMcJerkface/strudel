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