package com.nec.strudel.bench.micro.interactions.entity;

import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.interactions.base.AbstractCreatePost;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class CreatePost extends AbstractCreatePost<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		int userId = param.getInt(SessionParam.USER_ID);
		Post post = new Post(userId);
		post.setContent(param.get(InParam.CONTENT));
		db.create(post);
		return res.success();
	}

}
