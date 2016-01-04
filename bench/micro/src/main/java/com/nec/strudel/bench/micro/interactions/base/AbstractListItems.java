package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractListItems<T> implements Interaction<T> {

	public enum OutParam implements LocalParam {
		ITEM_LIST
	}


	/**
	 * Gets items by user ID (specified in the param as SessionParam.USER_ID).
	 * Set the list of Item instances as OutParam.ITEM_LIST.
	 * If the list is empty, return EMPTY_RESULT.
	 */
	@Override
	public abstract Result execute(Param param, T db, ResultBuilder res);

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		paramBuilder.use(SessionParam.USER_ID);
	}

	@Override
	public void complete(StateModifier modifier) {
		modifier.chooseSubset(TransitionParam.ITEM,
				SessionParam.NUM_UPDATE_ITEMS,
				OutParam.ITEM_LIST);
	}

}