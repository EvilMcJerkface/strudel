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

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.interactions.base.AbstractListItems.OutParam;
import com.nec.strudel.bench.micro.interactions.entity.ListItems;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.bench.test.EntityAssert;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(ListItems.class)
public class ListItemsTest extends AbstractInteractionTestBase {

	@Test
	public void testPrepare() {
		final int userId = 10;
		State state = newState()
		.put(SessionParam.USER_ID, userId);
    	Param param = prepare(state);
    	assertEquals(userId, param.getInt(SessionParam.USER_ID));
	}

	@Test
	public void testExecute() {
		final int userId = 10;
		final int size = 5;
		final int updates = 2;
		List<Item> data = populateList(Item.class, size,
				new ContentBuilder<Item>("userId", userId));

		Result res = executor()
				.param(SessionParam.USER_ID, userId)
				.executeSuccess();

		List<Item> items = res.get(OutParam.ITEM_LIST);
		EntityAssert.assertSameEntitySets(data, items);
		@SuppressWarnings("unchecked")
		List<Item> itemsChosen = 
				(List<Item>) completer(res)
				.state(SessionParam.NUM_UPDATE_ITEMS, updates)
				.complete()
				.get(TransitionParam.ITEM);
		assertNotNull(itemsChosen);
		assertEquals(updates, itemsChosen.size());
		for (Item item : itemsChosen) {
			EntityAssert.assertContains(item, data);
		}
	}
	@Test
	public void testExecuteEmpty() {
		final int userId = 101;

		Result res = executor()
				.param(SessionParam.USER_ID, userId)
				.executeSuccess(ResultMode.EMPTY_RESULT);

		List<Item> items = res.get(OutParam.ITEM_LIST);
		assertTrue(items.isEmpty());
		assertNull(complete(res).get(TransitionParam.ITEM));

	}
}
