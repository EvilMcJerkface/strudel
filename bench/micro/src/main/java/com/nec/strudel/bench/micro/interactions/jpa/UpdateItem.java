package com.nec.strudel.bench.micro.interactions.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdateItem;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class UpdateItem extends AbstractUpdateItem<EntityManager>
implements Interaction<EntityManager> {


	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		final List<ItemId> ids = param.getObjectList(InParam.ITEM_IDS);
		if (ids.isEmpty()) {
			return res.warn("parameter ITEM_ID is not set")
					.failure(ResultMode.MISSING_PARAM);
		}
		final String content = param.get(InParam.CONTENT);
		boolean hasUpdate = false;
		em.getTransaction().begin();
		for (ItemId id : ids) {
			Item item = em.find(Item.class, id,
					LockModeType.PESSIMISTIC_WRITE);
			if (item != null) {
				item.setContent(content);
				hasUpdate = true;
			}
		}
		em.getTransaction().commit();
		if (hasUpdate) {
			return res.success();
		} else {
			return res.success(ResultMode.EMPTY_RESULT);
		}
	}

}
