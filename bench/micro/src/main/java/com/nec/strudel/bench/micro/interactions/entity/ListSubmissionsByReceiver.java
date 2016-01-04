package com.nec.strudel.bench.micro.interactions.entity;

import java.util.List;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.interactions.base.AbstractListSubmissionsByReceiver;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ListSubmissionsByReceiver extends AbstractListSubmissionsByReceiver<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		int receiverId = param.getInt(InParam.RECEIVER_ID);
		List<Submission> submissions =
				db.getEntitiesByIndex(Submission.class,
					"receiverId", receiverId);

		res.set(TransitionParam.PEER_USER_ID, receiverId);
		res.set(OutParam.SUBMISSION_LIST, submissions);
		if (submissions.isEmpty()) {
			return res.success(ResultMode.EMPTY_RESULT);
		} else {
			return res.success();
		}
	}

}
