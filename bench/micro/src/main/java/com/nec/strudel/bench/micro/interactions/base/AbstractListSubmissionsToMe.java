package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractListSubmissionsToMe<T> implements Interaction<T> {

	public enum OutParam implements LocalParam {
		SUBMISSION_LIST
	}

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		paramBuilder.use(SessionParam.USER_ID);
	}

	@Override
	public void complete(StateModifier modifier) {
		Submission sub = modifier.getOne(OutParam.SUBMISSION_LIST);
		if (sub != null) {
			modifier.set(TransitionParam.PEER_USER_ID,
					sub.getSenderId());
		}
	}

}