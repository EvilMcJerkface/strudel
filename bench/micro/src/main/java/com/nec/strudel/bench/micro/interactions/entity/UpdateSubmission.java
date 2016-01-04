package com.nec.strudel.bench.micro.interactions.entity;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.entity.SubmissionId;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdateSubmission;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class UpdateSubmission extends AbstractUpdateSubmission<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		Submission sub = param.getObject(
				TransitionParam.SUBMISSION);
		if (sub == null) {
			return res.warn("missing param SUBMISSION")
			.failure(ResultMode.MISSING_PARAM);
		}
		final String content = param.get(InParam.CONTENT);
		final SubmissionId id = sub.getSubmissionId();
		boolean updated = db.run(sub, new EntityTask<Boolean>() {

			@Override
			public Boolean run(EntityTransaction tx) {
				Submission s = tx.get(Submission.class, id);
				if (s == null) {
					return false;
				}
				s.setContent(content);
				tx.update(s);
				return true;
			}
		});
		if (updated) {
			return res.success();
		} else {
			return res.success(ResultMode.EMPTY_RESULT);
		}
	}

}
