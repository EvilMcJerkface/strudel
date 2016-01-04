package com.nec.strudel.bench.micro.populate.entity;

import java.util.List;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.populate.SubmitSet;
import com.nec.strudel.bench.micro.populate.base.AbstractPopulateSubmission;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.workload.api.Populator;

public class PopulateSubmission extends AbstractPopulateSubmission<EntityDB>
implements Populator<EntityDB, SubmitSet> {

	@Override
	public void process(EntityDB db, SubmitSet param) {
		int userId = param.getSender();
		for (int i = 0; i < param.size(); i++) {
			int receiverId = param.getReceiver(i);
			Submission sub = new Submission(receiverId, userId);
			sub.setContent(param.getContent(i));
			db.create(sub);
		}
	}

	@Override
	protected List<Submission> getSubmissionsBySender(EntityDB db, int senderId) {
		return db.getEntitiesByIndex(Submission.class,
				"senderId", senderId);
	}
	@Override
	protected List<Submission> getSubmissionsByReceiver(EntityDB db, int receiverId) {
		return db.getEntitiesByIndex(
				Submission.class,
				"receiverId", receiverId);
	}

}
