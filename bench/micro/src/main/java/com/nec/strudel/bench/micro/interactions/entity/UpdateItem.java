/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
