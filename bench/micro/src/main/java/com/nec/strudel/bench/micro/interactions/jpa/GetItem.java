package com.nec.strudel.bench.micro.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.interactions.base.AbstractGetItem;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class GetItem extends AbstractGetItem<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		ItemId id = param.getObject(InParam.ITEM_ID);
		if (id == null) {
			return res.warn("ITEM_ID is not set")
			.failure(ResultMode.MISSING_PARAM);
		}
		Item item = em.find(Item.class, id);
		if (item != null) {
			res.set(TransitionParam.ITEM, item);
			return res.success();
		} else {
			return res.success(ResultMode.EMPTY_RESULT);
		}
	}

}
