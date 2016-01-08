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

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.entity.SharedId;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdateShared;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class UpdateShared extends AbstractUpdateShared<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		List<SharedId> ids = param.getObjectList(
				InParam.SHARED_IDS);
		if (ids.isEmpty()) {
			return res.warn("SHARED_ID is not set")
			.failure(ResultMode.MISSING_PARAM);
		}

		String content = param.get(InParam.CONTENT);
		boolean updated = false;
		em.getTransaction().begin();
		for (SharedId id : ids) {
			Shared shared = em.find(Shared.class, id,
					LockModeType.PESSIMISTIC_WRITE);
			if (shared != null) {
				updated = true;
				shared.setContent(content);
			}
		}
		em.getTransaction().commit();
		if (updated) {
			return res.success();
		} else {
			return res.success(ResultMode.EMPTY_RESULT);
		}
	}

}
