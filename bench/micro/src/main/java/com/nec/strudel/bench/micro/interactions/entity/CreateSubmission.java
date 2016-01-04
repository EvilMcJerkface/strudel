package com.nec.strudel.bench.micro.interactions.entity;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.interactions.base.AbstractCreateSubmission;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class CreateSubmission extends AbstractCreateSubmission<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		int userId = param.getInt(SessionParam.USER_ID);
		int receiverId = param.getInt(InParam.RECEIVER_ID);

		Submission sub = new Submission(receiverId, userId);
		sub.setContent(param.get(InParam.CONTENT));
		db.create(sub);
		return res.success();
	}

}
