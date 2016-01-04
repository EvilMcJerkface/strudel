package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractCreateItem<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		CONTENT,
	}

	/**
	 * Create an Item instance with user ID as SessionParam.USER_ID
	 * and content as InParam.CONTENT. an appropriate item number
	 * must be generated so that item-id = (user-id, item-number)
	 * is unique.
	 */
	@Override
	public abstract Result execute(Param param, T db, ResultBuilder res);


	@Override
	public void prepare(ParamBuilder paramBuilder) {
		paramBuilder.use(SessionParam.USER_ID)
		.randomAlphaString(InParam.CONTENT,
				SessionParam.CONTENT_LENGTH);
	}

	@Override
	public void complete(StateModifier modifier) {
		// do nothing
	}

}