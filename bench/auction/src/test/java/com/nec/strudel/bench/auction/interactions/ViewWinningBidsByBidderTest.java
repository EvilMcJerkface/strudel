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

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewWinningBidsByBidder.OutParam;
import com.nec.strudel.bench.auction.interactions.entity.ViewWinningBidsByBidder;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(ViewWinningBidsByBidder.class)
public class ViewWinningBidsByBidderTest extends AbstractInteractionTestBase {

    @Test
    public void testExecute() {
        final User seller = new User(1, "user1");
        final User bidder = new User(2, "user2");
        final AuctionItem item = initItem(seller);
        final Bid bid = initBid(bidder, item);

        populate(seller, bidder, item, bid);


        Result res = executor()
        		.param(SessionParam.USER_ID, bidder.getUserId())
        		.executeSuccess();

		List<AuctionItem> itemList = res.get(OutParam.WIN_ITEM_LIST);
		assertSingle(item, itemList);
		List<Bid> bidList = res.get(OutParam.WIN_BIDS);
		assertSingle(bid, bidList);
    }
	@Test
    public void testPrepare() {
	    final int bidderId = 2;

    	// prepare state
	    State state = newState()
    	.put(SessionParam.USER_ID, bidderId);
    	// execute prepare
    	Param param = prepare(state);
    	// Check prepare result
        assertEquals(bidderId,
        		param.getInt(SessionParam.USER_ID));
    }

	private Bid initBid(User bidder, AuctionItem item) {
	    final int bidNo = 1;
	    final double bidAmount = 10;

	    Bid bid = new Bid(item.getItemId(), bidNo);
        bid.setUserId(bidder.getUserId());
        bid.setBidAmount(bidAmount);
		return bid;
	}
    private AuctionItem initItem(User seller) {
        final int itemNo = 1;
        final double buyNow = 100;
        final double initialBid = 0;
        final double maxBid = 10;
        final long endDate = 0;

    	AuctionItem item =
    			new AuctionItem(seller.getUserId(), itemNo);
    	item.setBuyNow(buyNow);
    	item.setEndDate(endDate);
    	item.setInitialBid(initialBid);
    	item.setItemName("name");
        item.setMaxBid(maxBid);
        return item;
    }

}
