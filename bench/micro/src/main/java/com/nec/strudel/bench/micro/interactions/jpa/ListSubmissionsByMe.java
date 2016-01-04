package com.nec.strudel.bench.micro.interactions.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.interactions.base.AbstractListSubmissionsByMe;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ListSubmissionsByMe extends AbstractListSubmissionsByMe<EntityManager>
		implements Interaction<EntityManager> {
	static final String Q_BY_SENDER =
			"SELECT s FROM Submission s WHERE s.senderId = :uid";

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int uid = param.getInt(SessionParam.USER_ID);
		List<Submission> submissions =
				em.createQuery(Q_BY_SENDER, Submission.class)
				.setParameter("uid", uid).getResultList();
		res.set(OutParam.SUBMISSION_LIST, submissions);

		/**
		 * NOTE the state may be given to another
		 * thread having a different EntityManager
		 */
		for (Submission s : submissions) {
			em.detach(s);
		}

		if (submissions.isEmpty()) {
			return res.success(ResultMode.EMPTY_RESULT);
		} else {
			return res.success();
		}
	}

}
