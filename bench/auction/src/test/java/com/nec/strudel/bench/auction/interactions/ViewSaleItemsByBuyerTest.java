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
package com.nec.strudel.bench.auction.interactions;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleItemsByBuyer.OutParam;
import com.nec.strudel.bench.auction.interactions.entity.ViewSaleItemsByBuyer;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(ViewSaleItemsByBuyer.class)
public class ViewSaleItemsByBuyerTest extends AbstractInteractionTestBase {

	@Test
    public void testExecute() {

    	final User seller = new User(1, "user1");
        final User buyer = new User(2, "user2");
        final SaleItem item = initItem(seller);
        final BuyNowSale bns = initBns(buyer, item);

        populate(seller, buyer, item, bns);

        Result res = executor()
        		.param(SessionParam.USER_ID, buyer.getUserId())
        		.executeSuccess();

		List<SaleItem> itemList =
        	res.get(OutParam.SALE_ITEM_LIST);
		assertSingle(item, itemList);
		List<BuyNowSale> bnsList =
        	res.get(OutParam.BNS_LIST);
		assertSingle(bns, bnsList);
	}
	@Test
    public void testPrepare() {
		final int buyerId = 2;

    	// prepare state
		State state = newState()
    	.put(SessionParam.USER_ID, buyerId);
    	// execute prepare
    	Param param = prepare(state);
    	// Check prepare result
        assertEquals(buyerId,
        		param.getInt(SessionParam.USER_ID));
    }

	private BuyNowSale initBns(User buyer, SaleItem item) {
	    final int bnsNo = 1;
        BuyNowSale bns = new BuyNowSale(item.getId(), bnsNo);
        bns.setBuyerId(buyer.getUserId());
        return bns;

	}
    private SaleItem initItem(User seller) {
        final int itemNo = 1;
        final double price = 10;
        final int qnty = 1;

        SaleItem item = new SaleItem(seller.getUserId(), itemNo);
    	item.setItemName("name");
    	item.setPrice(price);
    	item.setQnty(qnty);
    	return item;
    }


}
