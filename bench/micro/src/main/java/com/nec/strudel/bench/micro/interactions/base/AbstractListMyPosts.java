package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractListMyPosts<T> implements Interaction<T> {

	public enum OutParam implements LocalParam {
		POST_LIST
	}

	public AbstractListMyPosts() {
		super();
	}

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		paramBuilder.use(SessionParam.USER_ID);
	}

	@Override
	public void complete(StateModifier modifier) {
		modifier.choose(TransitionParam.POST,
				OutParam.POST_LIST);
	}

}