package com.nec.strudel.bench.micro.interactions.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.nec.strudel.bench.micro.entity.Item;
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

public abstract class AbstractUpdateItem<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		ITEM_IDS,
		CONTENT,
	}
	/**
	 * For each item specified with ItemId in ITEM_IDS,
	 * update its content with CONTENT. If there is no item
	 * found, return EMPTY_RESULT
	 */
	@Override
	public abstract Result execute(Param param, T db, ResultBuilder res);

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		List<Item> items = paramBuilder.getList(TransitionParam.ITEM);
		if (!items.isEmpty()) {
			List<ItemId> itemIds =
					new ArrayList<ItemId>(items.size());
			for (Item item : items) {
				itemIds.add(item.getItemId());
			}
			paramBuilder.set(InParam.ITEM_IDS, itemIds);
		} else {
			int userId = paramBuilder.getInt(SessionParam.USER_ID);
			Set<Integer> ids = paramBuilder.getRandomIntIdSet(
					SessionParam.NUM_UPDATE_ITEMS,
					SessionParam.MIN_SEQ_NO,
					SessionParam.ITEMS_PER_USER);
			List<ItemId> itemIds =
					new ArrayList<ItemId>(ids.size());
			for (Integer itemNo : ids) {
				itemIds.add(new ItemId(userId, itemNo));
			}
			paramBuilder.set(InParam.ITEM_IDS, itemIds);
		}
		paramBuilder.randomAlphaString(InParam.CONTENT,
				SessionParam.CONTENT_LENGTH);
	}

	@Override
	public void complete(StateModifier modifier) {
		// nothing to set
	}

}