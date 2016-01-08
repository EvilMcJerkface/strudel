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
package com.nec.strudel.bench.micro.interactions;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.interactions.base.AbstractListShared.InParam;
import com.nec.strudel.bench.micro.interactions.base.AbstractListShared.OutParam;
import com.nec.strudel.bench.micro.interactions.entity.ListShared;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.bench.test.EntityAssert;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(ListShared.class)
public class ListSharedTest extends AbstractInteractionTestBase {

	@Test
	public void testPrepare() {
		final int userId = 12;
		final int minSetId = 1;
		final int setNum = 10;
		State state = newState()
		.put(SessionParam.USER_ID, userId)
		.put(SessionParam.MIN_SET_ID, 1)
		.put(SessionParam.SET_NUM, 10);
		Param param = prepare(state);
		int setId = param.getInt(InParam.SET_ID);
		assertTrue(minSetId <= setId);
		assertTrue(setId < minSetId + setNum);
	}

	@Test
	public void testExecute() {
		final int setId = 12;
		final int size = 7;
		final int updates = 4;
		List<Shared> data = populateList(Shared.class,
				size, new ContentBuilder<Shared>("setId", setId));

		Result res = executor()
				.param(InParam.SET_ID, setId)
				.param(SessionParam.NUM_UPDATE_ITEMS, updates)
				.executeSuccess();

		List<Shared> items = res.get(OutParam.ENTITY_LIST);
		EntityAssert.assertSameEntitySets(data, items);
		@SuppressWarnings("unchecked")
		List<Shared> updated = (List<Shared>) completer(res)
			.state(SessionParam.NUM_UPDATE_ITEMS, updates)
				.complete()
				.get(TransitionParam.SHARED);
		assertEquals(updates, updated.size());
		for (Shared s : updated) {
			EntityAssert.assertContains(s, data);
		}
	}
}
