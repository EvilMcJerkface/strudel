package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractCreateSubmission<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		RECEIVER_ID,
		CONTENT,
	}

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		paramBuilder.use(SessionParam.USER_ID)
		.randomAlphaString(InParam.CONTENT,
				SessionParam.CONTENT_LENGTH);
		if (paramBuilder.defined(TransitionParam.PEER_USER_ID)) {
			paramBuilder.use(InParam.RECEIVER_ID,
					TransitionParam.PEER_USER_ID);
		} else {
			paramBuilder.randomIntId(InParam.RECEIVER_ID,
					SessionParam.MIN_USER_ID,
					SessionParam.USER_NUM,
					SessionParam.USER_ID);
		}
	}

	@Override
	public void complete(StateModifier modifier) {
	}

}