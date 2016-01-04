package com.nec.strudel.bench.micro.interactions.entity;

import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.interactions.base.AbstractGetPost;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class GetPost extends AbstractGetPost<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		ItemId id = param.getObject(InParam.ITEM_ID);
		if (id == null) {
			return res.warn("ITEM_ID is not set")
			.failure(ResultMode.MISSING_PARAM);
		}
		Post post = db.get(Post.class, id);
		if (post != null) {
			res.set(OutParam.POST, post);
			return res.success();
		} else {
			return res.success(ResultMode.EMPTY_RESULT);
		}
	}

}
