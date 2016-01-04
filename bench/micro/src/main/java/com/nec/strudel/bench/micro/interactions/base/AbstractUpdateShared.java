package com.nec.strudel.bench.micro.interactions.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.entity.SharedId;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractUpdateShared<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		SHARED_IDS, // list of IDs in the SAME set.
		CONTENT,
	}

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		List<Shared> itemList =
				paramBuilder.getList(TransitionParam.SHARED);
		if (!itemList.isEmpty()) {
			List<SharedId> idList =
					new ArrayList<SharedId>(
							itemList.size());
			for (Shared s : itemList) {
				idList.add(s.getSharedId());
			}
			paramBuilder.set(InParam.SHARED_IDS,
					idList);
		} else {
			int setId = paramBuilder.getRandomIntId(
					SessionParam.MIN_SET_ID,
					SessionParam.SET_NUM);
			Set<Integer> ids = paramBuilder.getRandomIntIdSet(
					SessionParam.NUM_UPDATE_ITEMS,
					SessionParam.MIN_SEQ_NO,
					SessionParam.ITEMS_PER_SET);
			List<SharedId> idList =
					new ArrayList<SharedId>(ids.size());
			for (Integer itemNo : ids) {
				idList.add(new SharedId(setId, itemNo));
			}
			paramBuilder.set(InParam.SHARED_IDS,
					idList);
		}
		paramBuilder
		.randomAlphaString(InParam.CONTENT,
				SessionParam.CONTENT_LENGTH);
	}

	@Override
	public void complete(StateModifier modifier) {
		// nothing
	}

}