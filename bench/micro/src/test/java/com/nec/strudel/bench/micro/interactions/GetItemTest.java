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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.interactions.base.AbstractGetItem.InParam;
import com.nec.strudel.bench.micro.interactions.entity.GetItem;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.bench.test.EntityAssert;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(GetItem.class)
public class GetItemTest extends AbstractInteractionTestBase {
	/**
	 * ITEM_ID is chosen
	 * for the current user randomly from the items
	 * per user.
	 */
	@Test
	public void testPrepare() {
		final int userId = 10;
		final int minSeqNo = 1;
		final int itemsPerUser = 2;
		State state = newState()
		.put(SessionParam.USER_ID, userId)
		.put(SessionParam.MIN_SEQ_NO, minSeqNo)
		.put(SessionParam.ITEMS_PER_USER, itemsPerUser);
    	Param param = prepare(state);
    	ItemId id = param.getObject(InParam.ITEM_ID);
    	assertNotNull(id);
    	assertEquals(userId, id.getUserId());
    	assertTrue(minSeqNo <= id.getItemNo());
    	assertTrue(id.getItemNo() < minSeqNo + itemsPerUser);
	}

	@Test
	public void testExecute() {
		final int userId = 14;
		Item item = populateNew(Item.class,
				new ContentBuilder<Item>("userId", userId));

		Result res = executor()
				.param(InParam.ITEM_ID, item.getItemId())
				.executeSuccess();

		EntityAssert.assertEntityEquals(item,
				res.get(TransitionParam.ITEM));

		EntityAssert.assertEntityEquals(item,
				complete(res).get(TransitionParam.ITEM));
	}

	@Test
	public void testExecuteEmpty() {
		final int userId = 4;
		final int itemNo = 2;

		Result res = executor()
				.param(InParam.ITEM_ID, new ItemId(userId, itemNo))
				.executeSuccess(ResultMode.EMPTY_RESULT);

		assertNull(res.get(TransitionParam.ITEM));

		Item item2 = (Item) complete(res).get(TransitionParam.ITEM);
		assertNull(item2);
	}
}
