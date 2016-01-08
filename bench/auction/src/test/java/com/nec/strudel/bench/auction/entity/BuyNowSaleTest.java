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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.entity.EntityDescriptor;
import com.nec.strudel.entity.EntityGroup;
import com.nec.strudel.entity.KeyGeneratorType;

public class BuyNowSaleTest {

	@Test
	public void testKeyGen() {
		EntityDescriptor desc = EntityGroup.descriptor(BuyNowSale.class);
		KeyGeneratorType kgen = desc.keyGenerator();
		assertNotNull(kgen);
		assertFalse(kgen.isInGroup());
		BuyNowSale bns = new BuyNowSale(new ItemId(1,4));
		int bnsNo = 7;
		kgen.setKey(bns, bnsNo);
		assertEquals(bnsNo, bns.getBnsNo());
	}
}
