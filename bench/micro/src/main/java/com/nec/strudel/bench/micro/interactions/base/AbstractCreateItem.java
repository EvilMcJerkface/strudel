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

import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractCreateItem<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		CONTENT,
	}

	/**
	 * Create an Item instance with user ID as SessionParam.USER_ID
	 * and content as InParam.CONTENT. an appropriate item number
	 * must be generated so that item-id = (user-id, item-number)
	 * is unique.
	 */
	@Override
	public abstract Result execute(Param param, T db, ResultBuilder res);


	@Override
	public void prepare(ParamBuilder paramBuilder) {
		paramBuilder.use(SessionParam.USER_ID)
		.randomAlphaString(InParam.CONTENT,
				SessionParam.CONTENT_LENGTH);
	}

	@Override
	public void complete(StateModifier modifier) {
		// do nothing
	}

}