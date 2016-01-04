package com.nec.strudel.bench.micro.interactions.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.interactions.base.AbstractListSubmissionsToMe;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ListSubmissionsToMe extends AbstractListSubmissionsToMe<EntityManager>
		implements Interaction<EntityManager> {
	static final String Q_BY_RECEIVER =
			"SELECT s FROM Submission s WHERE s.receiverId = :uid";

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int userId = param.getInt(SessionParam.USER_ID);
		List<Submission> submissions =
				em.createQuery(Q_BY_RECEIVER, Submission.class)
				.setParameter("uid", userId)
				.getResultList();
		/**
		 * NOTE the state may be given to another
		 * thread having a different EntityManager
		 */
		detach(em, submissions);

		res.set(OutParam.SUBMISSION_LIST, submissions);
		if (submissions.isEmpty()) {
			return res.success(ResultMode.EMPTY_RESULT);
		} else {
			return res.success();
		}
	}
	static void detach(EntityManager em, List<?> entities) {
		for (Object e : entities) {
			em.detach(e);
		}
	}

}
