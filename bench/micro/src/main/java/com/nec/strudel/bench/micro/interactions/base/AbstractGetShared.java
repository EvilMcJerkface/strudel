package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.entity.SharedId;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractGetShared<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		SHARED_ID,
	}

	public AbstractGetShared() {
		super();
	}

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		int setId = paramBuilder.getRandomIntId(
				SessionParam.MIN_SET_ID,
				SessionParam.SET_NUM);
		int itemNo = paramBuilder.getRandomIntId(
				SessionParam.MIN_SEQ_NO,
				SessionParam.ITEMS_PER_SET);
		paramBuilder.set(InParam.SHARED_ID,
				new SharedId(setId, itemNo));
	}

	@Override
	public void complete(StateModifier modifier) {
		modifier.export(TransitionParam.SHARED);
	}

}