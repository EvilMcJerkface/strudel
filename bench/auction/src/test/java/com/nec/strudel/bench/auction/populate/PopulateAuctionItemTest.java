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
package com.nec.strudel.bench.auction.populate;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.populate.AuctionParamSet;
import com.nec.strudel.bench.auction.populate.AuctionParamSet.AuctionParam;
import com.nec.strudel.bench.auction.populate.entity.PopulateAuctionItem;
import com.nec.strudel.bench.test.EntityAssert;
import com.nec.strudel.bench.test.populate.AbstractPopulateTestBase;
import com.nec.strudel.bench.test.populate.TestOn;

@TestOn(PopulateAuctionItem.class)
public class PopulateAuctionItemTest extends AbstractPopulateTestBase<AuctionParamSet> {
	/**
	 * For simplicity of unit testing, we focus on the
	 * case when there are only two users. When we populate for
	 * one user (= seller), there must be only one buyer (the other user).
	 */
	static final int USER_NUM = 2;
	static final int SELLER_ID = 1;
	static final int BUYER_ID = 2;

	@Test
    public void testPopulate() {
        AuctionParamSet aps =
        		new AuctionItemParams(SELLER_ID, USER_NUM)
        .create();
    	process(aps);
        checkConsistencyWithParams(aps);
    }
    /**
     * different number of items per user.
     */
    @Test
    public void testPopulateMoreItems() {
        AuctionParamSet aps =
        		new AuctionItemParams(SELLER_ID, USER_NUM)
        .setItemsPerUser(10)
        .create();

        process(aps);
        checkConsistencyWithParams(aps);
    }
    /**
     * different number of bids per item.
     */
    @Test
    public void testPopulateMoreBids() {
        AuctionParamSet aps = 
        		new AuctionItemParams(SELLER_ID, USER_NUM)
        		.setNumOfBids(10).create();
        process(aps);
        checkConsistencyWithParams(aps);
    }
    @Test
    public void testPopulateNoBids() {
        AuctionParamSet aps =
        		new AuctionItemParams(SELLER_ID, USER_NUM)
				.setNumOfBids(0).create();
        process(aps);
        checkConsistencyWithParams(aps);
    }
    /**
     * Control that no BNA is created for items.
     */
    @Test
    public void testPopulateNoBNA() {
        AuctionParamSet aps =
        		new AuctionItemParams(SELLER_ID, USER_NUM)
        		.setBuynowRatio(0).create();
        process(aps);
        checkConsistencyWithParams(aps);
		assertEmpty(BuyNowAuction.class,
				"buyerId", BUYER_ID);	
    }

    private void checkConsistencyWithParams(AuctionParamSet aps) {
    	List<AuctionItem> expected = expectedItems(aps);

        checkSellerAuctionIndex(aps.sellerId(), expected);

        List<Bid> allBids = new ArrayList<Bid>();
        int idx = 0;
        AuctionParam[] ps = aps.getParams();
        for (AuctionItem item : expected) {
        	ItemId iid = item.getItemId();
	        assertGetEquals(item, AuctionItem.class, iid);

	        List<Bid> expectedBids = expectedBids(iid, ps[idx]);
	        allBids.addAll(expectedBids);
        	for (Bid b : expectedBids) {
            	assertGetEquals(b, Bid.class, b.getId());
        	}
	        checkAuctionBidIndex(iid, expectedBids);

            checkBuyNowAuction(iid, ps[idx].bnaIfExists(item));
            idx++;
        }
        checkBidderBidIndex(BUYER_ID, allBids);
    }

    protected List<AuctionItem> expectedItems(AuctionParamSet aps) {
    	List<AuctionItem> resultItems = new ArrayList<AuctionItem>();
    	AuctionParam[] ps = aps.getParams();
    	for (int i = 0; i < ps.length; i++) {
	    	AuctionItem expectedItem =
	    		new AuctionItem(aps.sellerId(), i + 1);
	    	ps[i].build(expectedItem);
	        resultItems.add(expectedItem);
    	}
        return resultItems;
    	
    }
    protected List<Bid> expectedBids(ItemId iid, AuctionParam p) {
    	List<Bid> bids = new ArrayList<Bid>();
    	for (int i = 1; i <= p.numOfBids(); i++) {
    		bids.add(new Bid(iid, i));
    	}
		p.build(bids);
    	return bids;
    }
    

    public void checkBidderBidIndex(int bidderId, List<Bid> expected) {
    	EntityAssert.assertSameEntitySets(expected, 
    			getList(Bid.class, "userId", bidderId));
    }
    public void checkAuctionBidIndex(ItemId iid, List<Bid> expected) {
    	EntityAssert.assertSameEntitySets(expected, 
    			getList(Bid.class, "auctionItemId", iid));
    }
 
    public void checkSellerAuctionIndex(int sellerId, List<AuctionItem> expected) {
    	EntityAssert.assertSameEntitySets(expected,
    			getList(AuctionItem.class, "sellerId", sellerId));
    }
    
    public void checkBuyNowAuction(ItemId iid, BuyNowAuction expected) {
    	if (expected == null) {
        	assertGetNull(BuyNowAuction.class, iid);
    	} else {
        	assertGetEquals(expected, BuyNowAuction.class, iid);
            assertIndexed(expected, "buyerId");
    	}
    }

}
