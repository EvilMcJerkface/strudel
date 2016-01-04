package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractUpdateSubmission<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		CONTENT,
	}

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		paramBuilder.use(TransitionParam.SUBMISSION)
		.randomAlphaString(InParam.CONTENT,
				SessionParam.CONTENT_LENGTH);
	}

	@Override
	public void complete(StateModifier modifier) {
	}

}