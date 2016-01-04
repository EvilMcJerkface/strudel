package com.nec.strudel.bench.micro.populate.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.populate.SubmitSet;
import com.nec.strudel.bench.micro.populate.base.AbstractPopulateSubmission;
import com.nec.strudel.workload.api.Populator;

public class PopulateSubmission extends AbstractPopulateSubmission<EntityManager>
implements Populator<EntityManager, SubmitSet> {

	@Override
	public void process(EntityManager em, SubmitSet param) {
		int userId = param.getSender();
		em.getTransaction().begin();
		for (int i = 0; i < param.size(); i++) {
			int receiverId = param.getReceiver(i);
			/**
			 * TODO FIXME submitNo of SubmissionId
			 */
			Submission sub = new Submission(receiverId, userId, i);
			sub.setContent(param.getContent(i));
			em.persist(sub);
		}
		em.getTransaction().commit();
	}
	static final String Q_BY_SENDER =
			"SELECT s FROM Submission s WHERE s.senderId = :uid";
	static final String Q_BY_RECEIVER =
			"SELECT s FROM Submission s WHERE s.receiverId = :uid";
	static final String PARAM_UID = "uid";

	@Override
	protected List<Submission> getSubmissionsBySender(EntityManager em,
			int senderId) {
		return em.createQuery(Q_BY_SENDER, Submission.class)
				.setParameter(PARAM_UID, senderId)
				.getResultList();
	}

	@Override
	protected List<Submission> getSubmissionsByReceiver(EntityManager em,
			int receiverId) {
		return em.createQuery(Q_BY_RECEIVER, Submission.class)
				.setParameter(PARAM_UID, receiverId)
				.getResultList();
	}

}
