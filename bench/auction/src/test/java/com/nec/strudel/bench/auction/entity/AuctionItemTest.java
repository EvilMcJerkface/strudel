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
