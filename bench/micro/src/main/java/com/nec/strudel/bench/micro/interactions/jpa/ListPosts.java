package com.nec.strudel.bench.micro.interactions.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.interactions.base.AbstractListPosts;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ListPosts extends AbstractListPosts<EntityManager>
implements Interaction<EntityManager> {
	static final String QUERY =
			"SELECT e FROM Post e WHERE e.userId = :uid";
	static final String PARAM_UID = "uid";

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int otherId = param.getInt(InParam.POSTER_ID);
		List<Post> posts = em.createQuery(QUERY, Post.class)
				.setParameter(PARAM_UID, otherId)
				.getResultList();
		res.set(OutParam.POST_LIST, posts);
		if (posts.isEmpty()) {
			return res.success(ResultMode.EMPTY_RESULT);
		} else {
			return res.success();
		}
	}

}
