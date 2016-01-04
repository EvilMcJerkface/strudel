package com.nec.strudel.bench.auction.interactions;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionItemsByBuyer.OutParam;
import com.nec.strudel.bench.auction.interactions.entity.ViewAuctionItemsByBuyer;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(ViewAuctionItemsByBuyer.class)
public class ViewAuctionItemsByBuyerTest extends AbstractInteractionTestBase {

    @Test
    public void testExecute() {
        final User seller = new User(1, "user1");
        final User buyer = new User(2, "user2");
        final AuctionItem item = initItem(seller);
        final BuyNowAuction bna = initBna(buyer, item);

        populate(seller, buyer, item, bna);

        Result res = executor()
        		.param(SessionParam.USER_ID, buyer.getUserId())
        		.executeSuccess();

		List<AuctionItem> itemList =
        	res.get(OutParam.AUCTION_ITEM_LIST);
		assertSingle(item, itemList);

    	List<BuyNowAuction> bnaList =
        	res.get(OutParam.BNA_LIST);
    	assertSingle(bna, bnaList);
	}
	@Test
    public void testPrepare() {
		final int buyerId = 1;
		State state = newState()
    	.put(SessionParam.USER_ID, buyerId);

    	Param param = prepare(state);

        assertEquals(buyerId,
        		param.getInt(SessionParam.USER_ID));
    }

	private BuyNowAuction initBna(User buyer, AuctionItem item) {
        BuyNowAuction bna = new BuyNowAuction(item.getItemId());
        bna.setBuyerId(buyer.getUserId());
        return bna;
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
