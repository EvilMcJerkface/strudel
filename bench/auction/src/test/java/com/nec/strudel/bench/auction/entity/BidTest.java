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
package com.nec.strudel.bench.auction.entity;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.BidId;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.entity.EntityDescriptor;
import com.nec.strudel.entity.EntityGroup;
import com.nec.strudel.entity.IndexType;
import com.nec.strudel.entity.KeyGeneratorType;

public class BidTest {

	@Test
	public void testGroupName() {
		EntityDescriptor itemDesc = EntityGroup.descriptor(AuctionItem.class);
		EntityDescriptor desc = EntityGroup.descriptor(Bid.class);
		assertEquals(itemDesc.getGroupName(), desc.getGroupName());
	}

	@Test
	public void testIndexes() {
		EntityDescriptor desc = EntityGroup.descriptor(Bid.class);
		IndexType index = desc.indexOn("auctionItemId");
		assertNotNull(index);
		assertEquals(desc.getGroupName(), index.getGroupName());
		assertEquals("auction_bid_idx", index.getName());
		ItemId iid = new ItemId(11, 3);
		Bid bid = new Bid(iid, 7);
		BidId bidid = bid.getId();
		assertEquals(iid, index.getIndexKey(bid));
		assertEquals(bidid, desc.getKey(bid));
		assertEquals(iid, desc.toGroupKey(bidid));
		assertEquals(iid, index.toGroupKey(iid));
	}
	@Test
	public void testKeyGen() {
		EntityDescriptor desc = EntityGroup.descriptor(Bid.class);
		KeyGeneratorType kgen = desc.keyGenerator();
		assertNotNull(kgen);
		assertFalse(kgen.isInGroup());
		ItemId iid = new ItemId(11, 3);
		Bid bid = new Bid(iid);
		int bidNo = 4;
		kgen.setKey(bid, bidNo);
		assertEquals(bidNo, bid.getBidNo());
	}
}
