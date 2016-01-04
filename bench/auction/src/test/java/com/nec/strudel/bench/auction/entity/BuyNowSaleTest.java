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
