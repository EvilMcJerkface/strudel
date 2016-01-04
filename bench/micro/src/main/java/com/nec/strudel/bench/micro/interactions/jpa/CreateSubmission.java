package com.nec.strudel.bench.micro.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.interactions.base.AbstractCreateSubmission;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class CreateSubmission extends AbstractCreateSubmission<EntityManager>
implements Interaction<EntityManager> {
	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int userId = param.getInt(SessionParam.USER_ID);
		int receiverId = param.getInt(InParam.RECEIVER_ID);
		String content = param.get(InParam.CONTENT);
		Submission sub = new Submission(receiverId, userId);
		sub.setContent(content);
		em.getTransaction().begin();
		em.persist(sub);
		em.getTransaction().commit();
		return res.success();
	}

}
