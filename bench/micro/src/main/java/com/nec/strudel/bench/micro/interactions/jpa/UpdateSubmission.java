package com.nec.strudel.bench.micro.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdateSubmission;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class UpdateSubmission extends AbstractUpdateSubmission<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		Submission sub = param.getObject(
				TransitionParam.SUBMISSION);
		if (sub == null) {
			return res.warn("missing param SUBMISSION")
			.failure(ResultMode.MISSING_PARAM);
		}
		final String content = param.get(InParam.CONTENT);
		sub.setContent(content);
		em.merge(sub);
		return res.success();
	}

}
