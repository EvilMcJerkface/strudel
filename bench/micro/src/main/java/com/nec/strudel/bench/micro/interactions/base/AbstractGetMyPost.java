package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractGetMyPost<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		ITEM_ID,
	}


	@Override
	public void prepare(ParamBuilder paramBuilder) {
		int userId = paramBuilder.getInt(SessionParam.USER_ID);
		int itemNo = paramBuilder.getRandomIntId(
				SessionParam.MIN_SEQ_NO,
				SessionParam.POSTS_PER_USER);
		paramBuilder.set(InParam.ITEM_ID,
				new ItemId(userId, itemNo));
	}

	@Override
	public void complete(StateModifier modifier) {
		modifier.export(TransitionParam.POST);
	}

}