package com.nec.strudel.bench.auction.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewBidsByBidder.OutParam;
import com.nec.strudel.bench.auction.interactions.entity.ViewBidsByBidder;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.impl.State;

@TestOn(ViewBidsByBidder.class)
public class ViewBidsByBidderTest extends AbstractInteractionTestBase {

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

        List<AuctionItem> itemList =
        	res.get(OutParam.AUCTION_ITEM_LIST);
        assertSingle(item, itemList);

		List<Bid> bidList = res.get(OutParam.BID_LIST);
		assertSingle(bid, bidList);
    }
	@Test
    public void testPrepare() {
		final int bidderId = 10;
		State state = newState()
    	.put(SessionParam.USER_ID, bidderId);

    	Param param = prepare(state);

        assertEquals(bidderId,
        		param.getInt(SessionParam.USER_ID));
    }
	@Test
    public void testComplete() {
		int sellerId = 1;
		int itemNo = 1;
		AuctionItem item = new AuctionItem(sellerId, itemNo);
		ResultBuilder res = new ResultBuilder();
    	// prepare res
    	List<AuctionItem> itemList = new ArrayList<AuctionItem>();
    	itemList.add(item);
    	res.set(OutParam.AUCTION_ITEM_LIST, itemList);
    	// execute complete
    	State state = complete(res.success());
    	// Check complete result
    	assertNotNull(state.get(TransParam.AUCTION_ITEM_ID));
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
        final double maxBid = -1;
        final long endDate = ParamUtil.now();
    	AuctionItem item = new AuctionItem(seller.getUserId(), itemNo);
    	item.setBuyNow(buyNow);
    	item.setEndDate(endDate);
    	item.setInitialBid(initialBid);
    	item.setItemName("name");
        item.setMaxBid(maxBid);
        return item;
    }

}
