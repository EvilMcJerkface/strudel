package com.nec.strudel.bench.micro.interactions.entity;

import java.util.List;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdateItem;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class UpdateItem extends AbstractUpdateItem<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		final List<ItemId> ids = param.getObjectList(InParam.ITEM_IDS);
		if (ids.isEmpty()) {
			return res.warn("parameter ITEM_ID is not set")
					.failure(ResultMode.MISSING_PARAM);
		}
		final String content = param.get(InParam.CONTENT);
		boolean updated = db.run(Item.class, ids.get(0),
					new EntityTask<Boolean>() {
				@Override
				public Boolean run(EntityTransaction tx) {
					boolean hasUpdate = false;
					for (ItemId id : ids) {
						Item item =
							tx.get(Item.class, id);
						if (item != null) {
							item.setContent(
								content);
							tx.update(item);
							hasUpdate = true;
						}
					}
					return hasUpdate;
				}
		});
		if (updated) {
			return res.success();
		} else {
			return res.success(ResultMode.EMPTY_RESULT);
		}
	}

}
