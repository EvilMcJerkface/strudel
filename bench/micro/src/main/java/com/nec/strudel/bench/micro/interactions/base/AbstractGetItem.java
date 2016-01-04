package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractGetItem<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		ITEM_ID,
	}


	/**
	 * Gets an Item by ID which is specified as InParam.ITEM_ID.
	 * If the item is found, set it to TransitionParam.ITEM.
	 * If it is not found, return EMPTY_RESULT.
	 */
	@Override
	public abstract Result execute(Param param, T db, ResultBuilder res);

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		int userId = paramBuilder.getInt(SessionParam.USER_ID);
		int itemNo = paramBuilder.getRandomIntId(
				SessionParam.MIN_SEQ_NO,
				SessionParam.ITEMS_PER_USER);
		paramBuilder.set(InParam.ITEM_ID,
				new ItemId(userId, itemNo));
	}

	@Override
	public void complete(StateModifier modifier) {
		modifier.export(TransitionParam.ITEM);
	}

}