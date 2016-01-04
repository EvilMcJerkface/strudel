package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractListShared<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		SET_ID
	}

	public enum OutParam implements LocalParam {
		ENTITY_LIST,
	}

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		paramBuilder.randomIntId(InParam.SET_ID,
				SessionParam.MIN_SET_ID, SessionParam.SET_NUM);
	}

	@Override
	public void complete(StateModifier modifier) {
		modifier.chooseSubset(TransitionParam.SHARED,
				SessionParam.NUM_UPDATE_ITEMS,
				OutParam.ENTITY_LIST);
	}

}