package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractListSubmissionsByReceiver<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		RECEIVER_ID,
	}

	public enum OutParam implements LocalParam {
		SUBMISSION_LIST,
	}


	@Override
	public void prepare(ParamBuilder paramBuilder) {
		paramBuilder.randomIntId(InParam.RECEIVER_ID,
				SessionParam.MIN_USER_ID,
				SessionParam.USER_NUM, SessionParam.USER_ID);
	}

	@Override
	public void complete(StateModifier modifier) {
		modifier.export(TransitionParam.PEER_USER_ID);
	}

}