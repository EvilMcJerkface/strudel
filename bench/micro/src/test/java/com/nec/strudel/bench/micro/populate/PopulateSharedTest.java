package com.nec.strudel.bench.micro.populate;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.params.DataParam;
import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.bench.micro.populate.entity.PopulateShared;
import com.nec.strudel.bench.test.populate.AbstractPopulateTestBase;
import com.nec.strudel.bench.test.populate.TestOn;

@TestOn(PopulateShared.class)
public class PopulateSharedTest extends AbstractPopulateTestBase<ContentSet> {

	@Test
	public void test() {
		final int sid = 7;
		final int contentLength = 23;
		final int itemsPerSet = 5;
		ContentSet cset = process(param(sid)
				.param(DataParam.CONTENT_LENGTH, contentLength)
				.param(DataParam.ITEMS_PER_SET, itemsPerSet));
		assertEquals(sid, cset.getGroupId());
		assertEquals(itemsPerSet, cset.getContents().length);
		for (String c : cset.getContents()) {
			assertEquals(contentLength, c.length());
		}
		List<Shared> items = getList(Shared.class, "setId", sid);
		assertEquals(itemsPerSet, items.size());
	}

}
