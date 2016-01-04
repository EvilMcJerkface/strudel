package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractCreateShared<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		SET_ID,
		CONTENT,
	}

	public AbstractCreateShared() {
		super();
	}

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		paramBuilder.randomIntId(InParam.SET_ID,
				SessionParam.MIN_SET_ID, SessionParam.SET_NUM)
				.randomAlphaString(InParam.CONTENT,
						SessionParam.CONTENT_LENGTH);
	}

	@Override
	public void complete(StateModifier modifier) {
		// do nothing
	}

}