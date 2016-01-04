package com.nec.strudel.bench.auction.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreAuctionBuyNow.InParam;
import com.nec.strudel.bench.auction.interactions.entity.StoreAuctionBuyNow;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.Executor;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.impl.State;

@TestOn(StoreAuctionBuyNow.class)
public class StoreAuctionBuyNowTest extends AbstractInteractionTestBase {


    @Test
    public void testExecute() {
    	final int buyerId = 2;
    	AuctionItem item = initItem();
    	populate(item);

    	executor(buyerId, item).executeSuccess();

    	BuyNowAuction bna = getSingle(
    			BuyNowAuction.class,"buyerId", buyerId);
		assertEquals(item.getItemId(), bna.getItemId());
		assertEquals(buyerId, bna.getBuyerId());
    	
		assertGetEquals(bna,
				BuyNowAuction.class, item.getItemId());

		AuctionItem updated =
    			getNotNull(AuctionItem.class, item.getItemId());
    	assertTrue(AuctionItem.isSold(updated));
    }
    @Test
    public void testBuyOnExpired() {
    	final int buyerId = 5;
    	AuctionItem item = initItem();
    	item.setEndDate(ParamUtil.dayBefore(1));
    	populate(item);
    	executor(buyerId, item)
    	.executeFailure(ResultMode.AUCTION_EXPIRED);
    }
    @Test
    public void testBuyOnSold() {
    	final int buyerId = 4;
    	AuctionItem item = initItem();
    	AuctionItem.sell(item);
    	populate(item);
    	executor(buyerId, item)
    	.executeFailure(ResultMode.AUCTION_SOLD);
    }
	@Test
    public void testPrepare() {
		final int buyerId = 3;
		AuctionItem item = initItem();
    	// prepare state
		State state = newState()
		    .put(TransParam.AUCTION_ITEM_ID, item.getItemId())
            .put(SessionParam.USER_ID, buyerId);
        // execute prepare
        Param param = prepare(state);
    	//Check prepare result
        assertEquals(item.getItemId(),
        		param.getObject(TransParam.AUCTION_ITEM_ID));
        assertEquals(buyerId,
        		param.getInt(SessionParam.USER_ID));
    }
    private AuctionItem initItem() {
    	final int sellerId = 1;
    	final int itemNo = 1;
    	final double maxBid = 10;
    	final double buyNow = 200;
    	final double initBid = 1;
    	final long endDate = ParamUtil.dayAfter(1);
    	AuctionItem item = new AuctionItem(sellerId, itemNo);
    	item.setBuyNow(buyNow);
    	item.setEndDate(endDate);
    	item.setInitialBid(initBid);
    	item.setItemName("name");
        item.setMaxBid(maxBid);
        return item;
    }

    private Executor<?> executor(int buyerId, AuctionItem item) {
    	return executor()
    			.param(TransParam.AUCTION_ITEM_ID,
    					item.getItemId())
    	    	.param(InParam.BNA_DATE,
    	    			ParamUtil.now())
    	    	.param(SessionParam.USER_ID,
    	    			buyerId);
    }

}
