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
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionItem.OutParam;
import com.nec.strudel.bench.auction.interactions.entity.ViewAuctionItem;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.impl.State;

@TestOn(ViewAuctionItem.class)
public class ViewAuctionItemTest extends AbstractInteractionTestBase {

    @Test
    public void testExecute() {
    	final User seller = new User(1, "user1");
        final AuctionItem item = initItem(seller);

        populate(seller, item);

        Result res = executor()
        		.param(TransParam.AUCTION_ITEM_ID, item.getItemId())
        		.executeSuccess();

        assertEquals(item.getMaxBid(),
        		(Double) res.get(TransParam.MAX_BID), 0.1);
        assertEquals(item.getBuyNow(),
        		(Double) res.get(TransParam.BUYNOW), 0.1);
        assertEquals(seller, res.get(OutParam.SELLER));
        /**
         * no buyer is retrieved when it is not sold
         */
        assertNull(res.get(OutParam.BUYER));
 
    }
    @Test
    public void testExecuteSold() {
    	final User seller = new User(1, "user1");
        final User buyer = new User(2, "user2");
        final AuctionItem item = initItem(seller);
        final BuyNowAuction bna = initBna(buyer, item);
        AuctionItem.sell(item);
        populate(seller, buyer, item, bna);


        Result res = executor()
        		.param(TransParam.AUCTION_ITEM_ID, item.getItemId())
        		.executeSuccess(ResultMode.AUCTION_SOLD);
        assertEquals(seller, res.get(OutParam.SELLER));
        assertEquals(buyer, res.get(OutParam.BUYER));
    }
	@Test
    public void testPrepare() {
		final ItemId id = new ItemId(1, 1);
    	// prepare state
		State state = newState()
    	.put(TransParam.AUCTION_ITEM_ID, id);
    	// execute prepare
    	Param param = prepare(state);
    	// Check prepare result
    	assertEquals(id,
        		param.getObject(TransParam.AUCTION_ITEM_ID));
    }
	@Test
    public void testComplete() {
		ResultBuilder res = new ResultBuilder();
    	// prepare res
    	res.set(TransParam.MAX_BID, 10.0);
    	res.set(TransParam.BUYNOW, 100.0);
    	// execute complete
    	State state = complete(res.success());
    	// Check complete result
    	assertEquals(10,
    			state.getDouble(TransParam.MAX_BID),
        		0.1);
    	assertEquals(100,
    			state.getDouble(TransParam.BUYNOW),
        		0.1);
    }
	private AuctionItem initItem(User seller) {
	    final int itemNo = 1;
	    final long endDate = ParamUtil.now();
	    final double initialBid = 0;
	    final double buyNow = 100;
	    final double maxBid = 50;

    	AuctionItem item = new AuctionItem(seller.getUserId(), itemNo);
    	item.setBuyNow(buyNow);
    	item.setEndDate(endDate);
    	item.setInitialBid(initialBid);
    	item.setItemName("name");
        item.setMaxBid(maxBid);
        return item;
    }
	private BuyNowAuction initBna(User buyer, AuctionItem item) {
        BuyNowAuction bna = new BuyNowAuction(item.getItemId());
        bna.setBuyerId(buyer.getUserId());
        bna.setBnaDate(ParamUtil.now());
        return bna;
	}

}
