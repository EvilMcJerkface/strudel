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
package com.nec.strudel.bench.micro.interactions.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdatePost;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class UpdatePost extends AbstractUpdatePost<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		final List<ItemId> ids = param.getObjectList(InParam.ITEM_ID);
		if (ids.isEmpty()) {
			return res.warn("parameter ITEM_ID is not set")
					.failure(ResultMode.MISSING_PARAM);
		}
		final String content = param.get(InParam.CONTENT);
		boolean hasUpdate = false;
		em.getTransaction().begin();
		for (ItemId id : ids) {
			Post item = em.find(Post.class, id,
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
