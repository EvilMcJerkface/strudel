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
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleBuyNowHistory.OutParam;
import com.nec.strudel.bench.auction.interactions.entity.ViewSaleBuyNowHistory;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(ViewSaleBuyNowHistory.class)
public class ViewSaleBuyNowHistoryTest extends AbstractInteractionTestBase {

    @Test
    public void testExecute() {
        final User buyer = new User(2, "user0");
        final SaleItem item = initItem();
        populate(buyer, item);
        final BuyNowSale bns = createBns(buyer, item);
        populate(bns);

        Result res = executor()
        		.param(TransParam.SALE_ITEM_ID, item.getId())
        		.executeSuccess();

        List<BuyNowSale> bnsList = res.get(OutParam.BNS_LIST);
        assertSingle(bns, bnsList);

		List<User> buyerList = res.get(OutParam.BUYER_LIST);
		assertSingle(buyer, buyerList);
    }
	@Test
    public void testPrepare() {
		SaleItem item = initItem();
    	// prepare state
		State state = newState()
    	.put(TransParam.SALE_ITEM_ID, item.getId());
    	// execute prepare
    	Param param = prepare(state);
    	// Check prepare result
        assertEquals(item.getId(),
        		param.getObject(TransParam.SALE_ITEM_ID));
    }
	private BuyNowSale createBns(User buyer, SaleItem item) {
	    final int qnty = 1;
        BuyNowSale bns = new BuyNowSale(item.getId());
        bns.setBuyerId(buyer.getUserId());
        bns.setQnty(qnty);
        return bns;
	}
    private SaleItem initItem() {
    	final int sellerId = 1;
    	final int itemNo = 1;
    	final int qnty = 10;
    	SaleItem item = new SaleItem(sellerId, itemNo);
    	item.setItemName("name");
    	item.setPrice(10);
    	item.setQnty(qnty);
    	return item;
    }

}
