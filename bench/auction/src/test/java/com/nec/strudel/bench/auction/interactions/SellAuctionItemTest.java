package com.nec.strudel.bench.auction.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractSellAuctionItem.InParam;
import com.nec.strudel.bench.auction.interactions.entity.SellAuctionItem;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.impl.State;

@TestOn(SellAuctionItem.class)
public class SellAuctionItemTest extends AbstractInteractionTestBase {


    @Test
    public void testExecute() {
        final int sellerId = 1;
        for (int itemNo = 1; itemNo <= 10; itemNo++) {
            AuctionItem item = expectedItem(sellerId, itemNo);

            executor()
            .param(InParam.NAME, item.getItemName())
    		.param(InParam.INITIAL_BID, item.getInitialBid())
    		.param(InParam.BUYNOW, item.getBuyNow())
    		.param(InParam.MAX_BID, item.getMaxBid())
    		.param(InParam.AUCTION_END_DATE, item.getEndDate())
    		.param(SessionParam.USER_ID, item.getSellerId())
    		.executeSuccess();

            assertGetEquals(item, AuctionItem.class, item.getItemId());

            List<AuctionItem> itemList =
    				getList(AuctionItem.class,
    						"sellerId",
    						sellerId);
            assertEquals(itemNo, itemList.size());
            assertTrue(itemList.contains(item));
        }
    }

    private AuctionItem expectedItem(int sellerId, int itemNo) {
    	double initialBid = 0;
        double buyNowRatio = 1;
        int daysToEnd = 1;
        String name = "auction1";

        AuctionItem item = new AuctionItem(sellerId, itemNo);
        item.setItemName(name);
        item.setInitialBid(initialBid);
        item.setBuyNow(initialBid * buyNowRatio);
        item.setMaxBid(initialBid);
        item.setEndDate(ParamUtil.dayAfter(daysToEnd));
        return item;
    }

	@Test
    public void testPrepare() {
	    final int sellerId = 1;
	    final int nameLen = 32;
		State state = newState();
    	// prepare state
    	state.put(SessionParam.USER_ID, sellerId);
    	state.put(SessionParam.AUCTION_INIT_BID_MIN, 0);
        state.put(SessionParam.AUCTION_INIT_BID_MAX, 10);
        state.put(SessionParam.AUCTION_BUY_NOW_RATIO_MIN, 0);
        state.put(SessionParam.AUCTION_BUY_NOW_RATIO_MAX, 10);
        state.put(SessionParam.AUCTION_DURATION_DATE_MIN, 0);
        state.put(SessionParam.AUCTION_DURATION_DATE_MAX, 10);
        state.put(SessionParam.ITEM_NAME_LEN, nameLen);
        // execute prepare
        Param param = prepare(state);
    	//Check prepare result
        assertNotNull(param.get(InParam.INITIAL_BID));
        assertNotNull(param.get(InParam.BUYNOW));
        assertNotNull(param.get(InParam.MAX_BID));
        assertNotNull(param.get(InParam.AUCTION_END_DATE));
        assertEquals(sellerId, param.getInt(SessionParam.USER_ID));
        String name = param.get(InParam.NAME);
        assertNotNull(name);
        assertEquals(nameLen, name.length());
    }

}
