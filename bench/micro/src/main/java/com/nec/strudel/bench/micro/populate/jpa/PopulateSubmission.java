/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
