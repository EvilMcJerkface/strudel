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

import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.entity.Post;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractUpdatePost<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		ITEM_ID,
		CONTENT,
	}

	public AbstractUpdatePost() {
		super();
	}

	@Override
	public void prepare(ParamBuilder paramBuilder) {
		Post post = paramBuilder.get(TransitionParam.POST);
		if (post != null) {
			paramBuilder.set(InParam.ITEM_ID, post.getItemId());
		} else {
			int userId = paramBuilder.getInt(SessionParam.USER_ID);
			int itemNo = paramBuilder.getRandomIntId(
					SessionParam.MIN_SEQ_NO,
					SessionParam.POSTS_PER_USER);
			paramBuilder.set(InParam.ITEM_ID,
					new ItemId(userId, itemNo));
		}
		paramBuilder.randomAlphaString(InParam.CONTENT,
				SessionParam.CONTENT_LENGTH);
	}

	@Override
	public void complete(StateModifier modifier) {
		// do nothing
	}

}