package com.nec.strudel.bench.micro.populate.entity;

import java.util.List;

import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.bench.micro.populate.base.AbstractPopulatePost;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.workload.api.Populator;

public class PopulatePost extends AbstractPopulatePost<EntityDB>
implements Populator<EntityDB, ContentSet> {

	@Override
	public void process(EntityDB db, final ContentSet param) {
		final int userId = param.getGroupId();
		db.run(Post.class, userId, new EntityTask<Void>() {

			@Override
			public Void run(EntityTransaction tx) {
				for (String c : param.getContents()) {
					Post post = new Post(userId);
					post.setContent(c);
					tx.create(post);
				}
				return null;
			}
		});
	}

	@Override
	protected List<Post> getPostsByUser(EntityDB db, int userId) {
		return db.getEntitiesByIndex(Post.class,
				"userId", userId);
	}
}
