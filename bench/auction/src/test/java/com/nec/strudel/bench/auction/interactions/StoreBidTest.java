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
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreBid.InParam;
import com.nec.strudel.bench.auction.interactions.entity.StoreBid;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.Executor;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.impl.State;

@TestOn(StoreBid.class)
public class StoreBidTest extends AbstractInteractionTestBase {


	@Test
    public void testExecute() {
		final int bidderId = 3;
    	AuctionItem item = initItem();
    	populate(item);
    	double bidAmount = item.getMaxBid() * 1.5;

    	executor(bidderId, item, bidAmount).executeSuccess();

    	Bid bid = consistentAccessSingleBid(bidderId, item);
 
    	assertEquals(bidAmount, bid.getBidAmount(), 0);

        AuctionItem updated =
        		getNotNull(AuctionItem.class, item.getItemId());
        assertEquals(bidAmount, updated.getMaxBid(), 0.1);
    }

    @Test
    public void testBidOnExpired() {
    	final int bidderId = 3;
    	AuctionItem item = initItem();
    	item.setEndDate(ParamUtil.dayBefore(1));
    	populate(item);
    	executor(bidderId, item, item.getMaxBid() * 2)
    	.executeFailure(ResultMode.AUCTION_EXPIRED);

    	assertGetEquals(item,
        		AuctionItem.class, item.getItemId());
    }
    @Test
    public void testBidOnSold() {
    	final int bidderId = 2;
    	AuctionItem item = initItem();
    	AuctionItem.sell(item);
    	populate(item);
    	executor(bidderId, item, item.getMaxBid() * 2)
    	.executeFailure(ResultMode.AUCTION_SOLD);

    	assertGetEquals(item,
        		AuctionItem.class, item.getItemId());
    }

    @Test
    public void testBidAmountLessThanMaxBid() {
    	final int bidderId = 2;
    	AuctionItem item = initItem();
        populate(item);
        double bidAmount = item.getMaxBid() / 2;

        executor(bidderId, item, bidAmount)
        .executeFailure(ResultMode.LOSING_BID);

        assertGetEquals(item,
        		AuctionItem.class, item.getItemId());
    }

    /**
     * Checks a single bid on an item. It must be consistently
     * accessed through primary key and two secondary key
     * (by item and bidder).
     * @param bidderId
     * @param item
     * @return the retrieved bid
     */
    protected Bid consistentAccessSingleBid(int bidderId, AuctionItem item) {
    	Bid bid = getSingle(Bid.class, "auctionItemId",
        		item.getItemId());
    	assertSingle(bid, "userId", bidderId);
    	assertGetEquals(bid, Bid.class, bid.getId());

        assertEquals(bidderId, bid.getUserId());
        return bid;
    }
	@Test
    public void testPrepare() {
		final int bidderId = 5;
		AuctionItem item = initItem();
    	// prepare state
		State state = newState()
    	.put(TransParam.BUYNOW, 200)
    	.put(TransParam.MAX_BID, 100)
        .put(TransParam.AUCTION_ITEM_ID, item.getItemId())
        .put(SessionParam.BID_AMOUNT_ADJUSTER, 1)
        .put(SessionParam.USER_ID, bidderId);
        // execute prepare
        Param param = prepare(state);
    	//Check prepare result
        assertEquals(item.getItemId(), 
        		param.getObject(TransParam.AUCTION_ITEM_ID));
        assertNotNull(param.get(InParam.BID_AMOUNT));
        assertNotNull(param.get(InParam.BID_DATE));
        assertEquals(bidderId, param.getInt(SessionParam.USER_ID));
    }

    private AuctionItem initItem() {
    	final int itemNo = 1;
        final int sellerId = 1;
    	final double maxBid = 10;
    	final long endDate = ParamUtil.dayAfter(1);

    	AuctionItem item = new AuctionItem(sellerId, itemNo);
    	item.setBuyNow(200);
    	item.setEndDate(endDate);
    	item.setInitialBid(1);
    	item.setItemName("name");
        item.setMaxBid(maxBid);
        return item;
    }
    private Executor<?> executor(int bidderId, AuctionItem item, double bidAmount) {
    	return executor()
    			.param(TransParam.AUCTION_ITEM_ID,
    	    			item.getItemId())
    	    	.param(TransParam.MAX_BID, item.getMaxBid())
    	    	.param(InParam.BID_DATE, ParamUtil.now())
    	    	.param(InParam.BID_AMOUNT, bidAmount)
    	        .param(SessionParam.USER_ID,
    	        		bidderId);	
    }
}
