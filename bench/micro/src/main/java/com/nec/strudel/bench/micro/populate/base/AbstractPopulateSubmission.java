package com.nec.strudel.bench.micro.populate.base;

import java.util.List;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.params.DataParam;
import com.nec.strudel.bench.micro.populate.SubmitSet;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.ValidateReporter;

public abstract class AbstractPopulateSubmission<T> implements Populator<T, SubmitSet> {

	@Override
	public String getName() {
		return "Submission";
	}

	@Override
	public SubmitSet createParameter(PopulateParam param) {
		int size = param.getInt(DataParam.SUBMISSIONS_PER_USER);
		int length = param.getInt(DataParam.CONTENT_LENGTH);
		int minUid = param.getInt(DataParam.MIN_USER_ID);
		int userNum = param.getInt(DataParam.USER_NUM);
		RandomSelector<Integer> receiver =
				RandomSelector.create(minUid, minUid + userNum);
		RandomSelector<String> content =
				RandomSelector.createAlphaString(length);
	
		return SubmitSet.create(param.getId(), size,
				receiver, content, param.getRandom());
	}
	@Override
	public boolean validate(T db, SubmitSet param,
			ValidateReporter reporter) {
		int userId = param.getSender();
		List<Submission> subs = getSubmissionsBySender(db, userId);
		if (subs.size() != param.size()) {
			reporter.error("# of submission for sender ("
					+ userId + ")"
					+ " must be " + param.size()
					+ " but " + subs.size());
			return false;
		}
		for (int i = 0; i < param.size(); i++) {
			int receiver = param.getReceiver(i);
			String content = param.getContent(i);
			boolean idFound = false;
			boolean found = false;
			for (Submission s : getSubmissionsByReceiver(db, receiver)) {
				if (s.getSenderId() == userId) {
					idFound = true;
					if (s.getContent().equals(content)) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				if (idFound) {
					reporter.error("submission from "
							+ userId
							+ " to " + receiver
							+ " found in "
							+ " the index but"
						+ " content is different");
				} else {
					reporter.error("submission from "
							+ userId
							+ " to " + receiver
							+ " not found in "
							+ " the index");
				}
				return false;
			}
		}
		return true;
	}
	protected abstract List<Submission> getSubmissionsBySender(T db, int senderId);
	protected abstract List<Submission> getSubmissionsByReceiver(T db, int receiverId);
}