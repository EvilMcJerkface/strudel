package com.nec.strudel.bench.micro.populate;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.params.DataParam;
import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.bench.micro.populate.entity.PopulateItem;
import com.nec.strudel.bench.test.populate.AbstractPopulateTestBase;
import com.nec.strudel.bench.test.populate.TestOn;

@TestOn(PopulateItem.class)
public class PopulateItemTest extends AbstractPopulateTestBase<ContentSet> {

	@Test
	public void test() {
		final int uid = 10;
		final int contentLength = 10;
		final int itemsPerUser = 4;
		ContentSet cset = process(param(uid)
				.param(DataParam.CONTENT_LENGTH, contentLength)
				.param(DataParam.ITEMS_PER_USER, itemsPerUser));
		assertEquals(uid, cset.getGroupId());
		assertEquals(itemsPerUser, cset.getContents().length);
		for (String c : cset.getContents()) {
			assertEquals(contentLength, c.length());
		}
		List<Item> items = getList(Item.class, "userId", uid);
		assertEquals(itemsPerUser, items.size());
	}
}
