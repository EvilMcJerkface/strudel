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