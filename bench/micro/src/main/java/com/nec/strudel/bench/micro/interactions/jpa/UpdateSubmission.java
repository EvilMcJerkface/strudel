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

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdateSubmission;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class UpdateSubmission extends AbstractUpdateSubmission<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		Submission sub = param.getObject(
				TransitionParam.SUBMISSION);
		if (sub == null) {
			return res.warn("missing param SUBMISSION")
			.failure(ResultMode.MISSING_PARAM);
		}
		final String content = param.get(InParam.CONTENT);
		sub.setContent(content);
		em.merge(sub);
		return res.success();
	}

}
