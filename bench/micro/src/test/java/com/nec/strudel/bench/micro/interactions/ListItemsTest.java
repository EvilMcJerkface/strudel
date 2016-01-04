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
