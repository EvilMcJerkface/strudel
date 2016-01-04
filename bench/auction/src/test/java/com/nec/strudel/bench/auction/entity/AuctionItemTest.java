package com.nec.strudel.bench.auction.entity;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.entity.EntityDescriptor;
import com.nec.strudel.entity.EntityGroup;
import com.nec.strudel.entity.IndexType;

public class AuctionItemTest {


	@Test
	public void testIndex() {
		EntityDescriptor desc = EntityGroup.descriptor(AuctionItem.class);
		IndexType index = desc.autoIndex();
		assertNotNull(index);
		assertFalse(index.getGroupName().equals(desc.getGroupName()));
		AuctionItem item = new AuctionItem(3, 5);
		ItemId iid = item.getItemId();
		assertEquals(iid, desc.getKey(item));
		assertEquals(iid, desc.toGroupKey(desc.getKey(item)));
		assertEquals(item.getSellerId(), index.getIndexKey(item));
		assertEquals(item.getSellerId(), index.toGroupKey(item.getSellerId()));
	}
}
