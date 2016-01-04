package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractGetPost<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		ITEM_ID,
	}

	public enum OutParam implements LocalParam {
		POST,
	}

	public AbstractGetPost() {
		super();
	}

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		int userId = paramBuilder.getRandomIntId(
				SessionParam.MIN_USER_ID,
				SessionParam.USER_NUM,
				SessionParam.USER_ID);
		int itemNo = paramBuilder.getRandomIntId(
				SessionParam.MIN_SEQ_NO,
				SessionParam.POSTS_PER_USER);
		paramBuilder.set(InParam.ITEM_ID,
				new ItemId(userId, itemNo));
	}

	@Override
	public void complete(StateModifier modifier) {
		// do nothing
	}

}