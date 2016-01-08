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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdateItem.InParam;
import com.nec.strudel.bench.micro.interactions.entity.UpdateItem;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.impl.State;

@TestOn(UpdateItem.class)
public class UpdateItemTest extends AbstractInteractionTestBase {

	@Test
	public void testPrepare() {
		final int userId = 10;
		final Item item = new Item();
		item.setUserId(userId);
		item.setItemNo(1);
		item.setContent("test");

		final int length = 20;
		State state = newState()
		.put(SessionParam.USER_ID, userId)
		.put(SessionParam.CONTENT_LENGTH, length)
		.put(TransitionParam.ITEM, item);
    	Param param = prepare(state);
    	String content = param.get(InParam.CONTENT);
    	assertNotNull(content);
    	assertEquals(length, content.length());
    	List<ItemId> ids = param.getObjectList(InParam.ITEM_IDS);
    	assertEquals(1, ids.size());
    	ItemId id = ids.get(0);
    	assertEquals(item.getItemId(), id);
	}
	@Test
	public void testPrepareMultiItems() {
		final int userId = 10;
		final int num = 5;
		List<Item> items = new ArrayList<Item>(num);
		Set<ItemId> idSet = new HashSet<ItemId>();
		for (int i = 1; i <= num; i++) {
			Item item = new Item();
			item.setUserId(userId);
			item.setItemNo(i);
			item.setContent("test" + i);
			items.add(item);
			idSet.add(item.getItemId());
		}

		final int length = 20;
		State state = newState()
		.put(SessionParam.USER_ID, userId)
		.put(SessionParam.CONTENT_LENGTH, length)
		.put(TransitionParam.ITEM, items);
    	Param param = prepare(state);
    	String content = param.get(InParam.CONTENT);
    	assertNotNull(content);
    	assertEquals(length, content.length());
    	List<ItemId> ids = param.getObjectList(InParam.ITEM_IDS);
    	assertEquals(num, ids.size());
    	for (ItemId id : ids) {
    		assertTrue(idSet.contains(id));
    	}
	}
	
	/**
	 * When ITEM is not set, ITEM_ID is chosen
	 * for the current user randomly from the items
	 * per user.
	 */
	@Test
	public void testPrepareRandomChoice() {
		final int userId = 10;
		final int length = 20;
		final int minSeqNo = 1;
		final int itemsPerUser = 5;
		final int numUpdate = 2;
		State state = newState()
		.put(SessionParam.USER_ID, userId)
		.put(SessionParam.CONTENT_LENGTH, length)
		.put(SessionParam.MIN_SEQ_NO, minSeqNo)
		.put(SessionParam.ITEMS_PER_USER, itemsPerUser)
		.put(SessionParam.NUM_UPDATE_ITEMS, numUpdate);
    	Param param = prepare(state);
    	String content = param.get(InParam.CONTENT);
    	assertNotNull(content);
    	assertEquals(length, content.length());
    	List<ItemId> ids = param.getObjectList(InParam.ITEM_IDS);
    	assertEquals(numUpdate, ids.size());
    	for (ItemId id : ids) {
        	assertNotNull(id);
        	assertEquals(userId, id.getUserId());
        	assertTrue(minSeqNo <= id.getItemNo());
        	assertTrue(id.getItemNo() < minSeqNo + itemsPerUser);
    	}
	}
	@Test
	public void testExecute() {
		final int userId = 14;
		final int size = 5;
		final String content = "test-content";
		List<Item> items = populateList(Item.class,
				size, new ContentBuilder<Item>("userId", userId));

		Item item = items.get(0);

		executor()
		.param(InParam.ITEM_IDS, item.getItemId())
		.param(InParam.CONTENT, content)
		.executeSuccess();

		Item updated = getNotNull(Item.class, item.getItemId());
		assertEquals(content, updated.getContent());
	}
	@Test
	public void testExecuteMulti() {
		final int userId = 14;
		final int size = 5;
		final int updateNum = 2;
		final String content = "test-content";
		List<Item> items = populateList(Item.class,
				size, new ContentBuilder<Item>("userId", userId));
		List<ItemId> updates = new ArrayList<ItemId>(updateNum);
		for (Item item : items) {
			updates.add(item.getItemId());
			if (updates.size() >= updateNum) {
				break;
			}
		}
		executor()
		.param(InParam.ITEM_IDS, updates)
		.param(InParam.CONTENT, content)
		.executeSuccess();

		for (ItemId id : updates) {
			Item updated = getNotNull(Item.class,
					id);
			assertEquals(content, updated.getContent());
		}
	}
}
