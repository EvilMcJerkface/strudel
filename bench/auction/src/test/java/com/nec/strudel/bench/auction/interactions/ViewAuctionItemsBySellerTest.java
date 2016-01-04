package com.nec.strudel.bench.auction.interactions;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionItemsBySeller.InParam;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionItemsBySeller.OutParam;
import com.nec.strudel.bench.auction.interactions.entity.ViewAuctionItemsBySeller;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.impl.State;

@TestOn(ViewAuctionItemsBySeller.class)
public class ViewAuctionItemsBySellerTest extends AbstractInteractionTestBase {

    @Test
    public void testExecute() {
    	User seller = new User(1, "user1");
        User buyer = new User(2, "user2");
        AuctionItem item = new AuctionItem(seller.getUserId());
        initItem(item);
        populate(seller, buyer, item);
        
        BuyNowAuction bna = initBna(buyer, item);
        populate(bna);
        Result res = executor()
        		.param(InParam.SELLER_ID, seller.getUserId())
        		.executeSuccess();

		List<AuctionItem> itemList = res.get(OutParam.AUCTION_ITEM_LIST);
		assertSingle(item, itemList);
	}
	@Test
    public void testPrepare() {
		final int userId = 1;
		final int totalUser = 100;
    	// prepare state
		State state = newState()
    	.put(SessionParam.TOTAL_USER, totalUser)
    	.put(SessionParam.USER_ID, userId);
    	// execute prepare
    	Param param = prepare(state);
    	//Check prepare result
        assertNotNull(param.getObject(InParam.SELLER_ID));
        assertNotSame(userId, param.getInt(InParam.SELLER_ID));
	}
	@Test
    public void testComplete() {
		int sellerId = 1;
		int itemNo = 1;
		AuctionItem item = new AuctionItem(sellerId, itemNo);
    	List<AuctionItem> itemList = Arrays.asList(item);

    	ResultBuilder res = new ResultBuilder()
    		.set(OutParam.AUCTION_ITEM_LIST, itemList);
    	State state = complete(res.success());

    	assertNotNull(state.get(TransParam.AUCTION_ITEM_ID));
    }
	private BuyNowAuction initBna(User buyer, AuctionItem item) {
        BuyNowAuction bna = new BuyNowAuction(item.getItemId());
        bna.setBuyerId(buyer.getUserId());
        return bna;
	}
    private void initItem(AuctionItem item) {
        final double buyNow = 100;
        final double initialBid = 0;
        final double maxBid = -1;
        final long endDate = ParamUtil.now();
    	item.setBuyNow(buyNow);
    	item.setEndDate(endDate);
    	item.setInitialBid(initialBid);
    	item.setItemName("name");
        item.setMaxBid(maxBid);
    }

}
