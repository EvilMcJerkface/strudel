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
package com.nec.strudel.bench.auction.interactions.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.jpa.ViewAuctionBidHistory;
import com.nec.strudel.bench.auction.interactions.jpa.ViewAuctionBuyNow;
import com.nec.strudel.bench.auction.interactions.jpa.ViewAuctionItem;
import com.nec.strudel.bench.auction.interactions.jpa.ViewAuctionItemsByBuyer;
import com.nec.strudel.bench.auction.interactions.jpa.ViewAuctionItemsBySeller;
import com.nec.strudel.bench.auction.interactions.jpa.ViewBidsByBidder;
import com.nec.strudel.bench.auction.interactions.jpa.ViewSaleBuyNowHistory;
import com.nec.strudel.bench.auction.interactions.jpa.ViewSaleItem;
import com.nec.strudel.bench.auction.interactions.jpa.ViewSaleItemsByBuyer;
import com.nec.strudel.bench.auction.interactions.jpa.ViewSaleItemsBySeller;
import com.nec.strudel.bench.auction.interactions.jpa.ViewUser;
import com.nec.strudel.bench.auction.interactions.jpa.ViewWinningBidsByBidder;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.bench.test.JpaDatabase;
import com.nec.strudel.bench.test.interactions.Executor;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Result;

public class ViewInteractionsTest {
	static final JpaDatabase JPA_DB = new JpaDatabase("auction");
	static final AtomicInteger USER_IDS = new AtomicInteger(1);
	private EntityManager em;
	@BeforeClass
	public static void startup() {
		JPA_DB.startup();
	}
	@AfterClass
	public static void shutdown() {
		JPA_DB.close();
	}
	@Before
	public void begin() {
		em = JPA_DB.createEntityManager();
	}
	@After
	public void end() {
		if (em != null) {
			em.clear();
		}
	}
	protected int newUserId() {
		return USER_IDS.getAndIncrement();
	}
	protected User newUser(String name) {
		User u = new User(newUserId(), name);
		accessor().store(u);
		return u;
	}
	protected AuctionItem newItem(User seller) {
        final int sellerId = seller.getUserId();
    	final double maxBid = 10;
    	final long endDate = ParamUtil.dayAfter(1);

    	AuctionItem item = new AuctionItem(sellerId);
    	item.setBuyNow(200);
    	item.setEndDate(endDate);
    	item.setInitialBid(1);
    	item.setItemName(seller.getUname() + "'s item");
        item.setMaxBid(maxBid);
        return item;
    }
	protected AuctionItem sellItem(User user) {
		AuctionItem item = newItem(user);
		AuctionAccessor acc = accessor();
		List<AuctionItem> existing =
				acc.auctionsBySeller(user.getUserId());
		acc.store(item);
		List<AuctionItem> aftersell =
				acc.auctionsBySeller(user.getUserId());
		for (AuctionItem a : aftersell) {
			if (!existing.contains(a)) {
				return a;
			}
		}
		return null; // fail
	}
	protected BuyNowAuction storeBna(User buyer, AuctionItem item) {
        BuyNowAuction bna = new BuyNowAuction(item.getItemId());
        bna.setBuyerId(buyer.getUserId());
        bna.setBnaDate(new Date().getTime());
        accessor().store(bna);
        return bna;
	}
	protected Bid storeBid(User bidder, AuctionItem item) {
        final double bidAmount = 10;
        Bid bid = new Bid(item.getItemId());
        bid.setUserId(bidder.getUserId());
        bid.setBidAmount(bidAmount);
		AuctionAccessor acc = accessor();
		List<Bid> before = acc.bidsByItem(item.getItemId());
		acc.store(bid);
		List<Bid> after = acc.bidsByItem(item.getItemId());
		for (Bid b : after) {
			if (!before.contains(b)) {
				return b;
			}
		}
        return null; // fail
	}
	protected BuyNowSale storeBns(User buyer, SaleItem item) {
	    final int qnty = 1;
        BuyNowSale bns = new BuyNowSale(item.getId());
        bns.setBuyerId(buyer.getUserId());
        bns.setQnty(qnty);
        accessor().store(bns);
        return bns;
	}
	protected SaleItem sellSaleItem(User seller) {
    	final int sellerId = seller.getUserId();
    	final int qnty = 10;
    	SaleItem item = new SaleItem(sellerId);
    	item.setItemName(seller.getUname()
    			+ "'s sale item");
    	item.setPrice(10);
    	item.setQnty(qnty);
		AuctionAccessor acc = accessor();
		List<SaleItem> existing =
				acc.salesBySeller(sellerId);
		acc.store(item);
		List<SaleItem> aftersell =
				acc.salesBySeller(sellerId);
		for (SaleItem s : aftersell) {
			if (!existing.contains(s)) {
				return s;
			}
		}
		return null; // fail
    }
	public Executor<EntityManager> executor(Interaction<EntityManager> interaction) {
		return new Executor<EntityManager>(interaction, em);
	}
	public AuctionAccessor accessor() {
		return new AuctionAccessor(em);
	}
	protected <T> T assertSingle(List<T> items) {
		assertEquals(1, items.size());
		return items.get(0);
	}

	@Test
	public void testViewUser() {
        final User user = newUser("user1");
        Result res = executor(new ViewUser())
		.param(SessionParam.USER_ID, user.getUserId())
		.executeSuccess();

        assertEquals(user, res.get(ViewUser.OutParam.USER));
	}
    @Test
    public void testAuctionBidHistory() {
        final User user = newUser("u1");
        final User seller = newUser("u2");
        AuctionItem item = sellItem(seller);
        final ItemId iid = item.getItemId();
        Bid bid = new Bid(iid);
        bid.setUserId(user.getUserId());
        accessor().store(bid);

        Result res = executor(new ViewAuctionBidHistory())
        		.param(TransParam.AUCTION_ITEM_ID, iid)
        		.executeSuccess();

		List<Bid> bidList = res.get(ViewAuctionBidHistory.OutParam.BID_LIST);
		Bid b = assertSingle(bidList);
		assertEquals(bid.getAuctionItemId(), b.getAuctionItemId());
		assertEquals(bid.getUserId(), user.getUserId());

		List<User> bidderList = res.get(ViewAuctionBidHistory.OutParam.BIDDER_LIST);
		User u = assertSingle(bidderList);
		assertEquals(user, u);
    }

    @Test
    public void testAuctionBuyNow() {
        User seller = newUser("u1");
        User buyer = newUser("u2");
        AuctionItem item = sellItem(seller);
        ItemId itemId = item.getItemId();
        BuyNowAuction bna = storeBna(buyer, item);

        Result res = executor(new ViewAuctionBuyNow())
        		.param(TransParam.AUCTION_ITEM_ID, itemId)
        		.executeSuccess();

        BuyNowAuction bna1 = res.get(ViewAuctionBuyNow.OutParam.BNA);
        assertEquals(bna, bna1);
        User user1 = res.get(ViewAuctionBuyNow.OutParam.BUYER);
        assertEquals(buyer, user1);
    }
    @Test
    public void testAuctionItemsByBuyer() {
        User seller = newUser("u1");
        User buyer = newUser("u2");
        AuctionItem item = sellItem(seller);
        BuyNowAuction bna = storeBna(buyer, item);

        Result res = executor(new ViewAuctionItemsByBuyer())
        		.param(SessionParam.USER_ID, buyer.getUserId())
        		.executeSuccess();

		List<AuctionItem> itemList =
        	res.get(ViewAuctionItemsByBuyer.OutParam.AUCTION_ITEM_LIST);
		assertEquals(item, assertSingle(itemList));

    	List<BuyNowAuction> bnaList =
        	res.get(ViewAuctionItemsByBuyer.OutParam.BNA_LIST);
    	assertEquals(bna, assertSingle(bnaList));
	}
    @Test
    public void testAuctionItemsBySeller() {
    	User seller = newUser("user1");
    	User buyer = newUser("user2");
    	AuctionItem item = sellItem(seller);

    	storeBna(buyer, item);
    	Result res = executor(new ViewAuctionItemsBySeller())
    			.param(ViewAuctionItemsBySeller.InParam.SELLER_ID,
    					seller.getUserId())
    			.executeSuccess();

    	List<AuctionItem> itemList = res.get(
    			ViewAuctionItemsBySeller.OutParam.AUCTION_ITEM_LIST);
    	assertEquals(item, assertSingle(itemList));
	}
    @Test
    public void testAuctionItem() {
    	final User seller = newUser("user1");
        final AuctionItem item = sellItem(seller);

        Result res = executor(new ViewAuctionItem())
        		.param(TransParam.AUCTION_ITEM_ID, item.getItemId())
        		.executeSuccess();

        assertEquals(item.getMaxBid(),
        		(Double) res.get(TransParam.MAX_BID), 0.1);
        assertEquals(item.getBuyNow(),
        		(Double) res.get(TransParam.BUYNOW), 0.1);
        assertEquals(seller, res.get(ViewAuctionItem.OutParam.SELLER));
        /**
         * no buyer is retrieved when it is not sold
         */
        assertNull(res.get(ViewAuctionItem.OutParam.BUYER));
 
    }
    @Test
    public void testAuctionItemSold() {
    	final User seller = newUser("user1");
        final User buyer = newUser("user2");
        final AuctionItem item = sellItem(seller);
        storeBna(buyer, item);
        AuctionItem.sell(item);
        accessor().store(item);

        Result res = executor(new ViewAuctionItem())
        		.param(TransParam.AUCTION_ITEM_ID, item.getItemId())
        		.executeSuccess(ResultMode.AUCTION_SOLD);
        assertEquals(seller, res.get(ViewAuctionItem.OutParam.SELLER));
        assertEquals(buyer, res.get(ViewAuctionItem.OutParam.BUYER));
    }
    @Test
    public void testBidsByBidder() {
    	final User seller = newUser("user1");
        final User bidder = newUser("user2");
        final AuctionItem item = sellItem(seller);
        final Bid bid = storeBid(bidder, item);

        Result res = executor(new ViewBidsByBidder())
        		.param(SessionParam.USER_ID, bidder.getUserId())
        		.executeSuccess();

        List<AuctionItem> itemList =
        	res.get(ViewBidsByBidder.OutParam.AUCTION_ITEM_LIST);
        assertEquals(item, assertSingle(itemList));

		List<Bid> bidList = res.get(ViewBidsByBidder.OutParam.BID_LIST);
		assertEquals(bid, assertSingle(bidList));
    }
    @Test
    public void testSaleBuyNowHistory() {
    	final User seller = newUser("seller1");
        final User buyer = newUser("user0");
        final SaleItem item = sellSaleItem(seller);
        final BuyNowSale bns = storeBns(buyer, item);

        Result res = executor(new ViewSaleBuyNowHistory())
        		.param(TransParam.SALE_ITEM_ID, item.getId())
        		.executeSuccess();

        List<BuyNowSale> bnsList = res.get(ViewSaleBuyNowHistory.OutParam.BNS_LIST);
        assertEquals(bns, assertSingle(bnsList));

		List<User> buyerList = res.get(ViewSaleBuyNowHistory.OutParam.BUYER_LIST);
		assertEquals(buyer, assertSingle(buyerList));
    }
	@Test
    public void testSaleItemsByBuyer() {

    	final User seller = newUser("user1");
        final User buyer = newUser("user2");
        final SaleItem item = sellSaleItem(seller);
        final BuyNowSale bns = storeBns(buyer, item);

        Result res = executor(new ViewSaleItemsByBuyer())
        		.param(SessionParam.USER_ID, buyer.getUserId())
        		.executeSuccess();

		List<SaleItem> itemList =
        	res.get(ViewSaleItemsByBuyer.OutParam.SALE_ITEM_LIST);
		assertEquals(item, assertSingle(itemList));
		List<BuyNowSale> bnsList =
        	res.get(ViewSaleItemsByBuyer.OutParam.BNS_LIST);
        assertEquals(bns, assertSingle(bnsList));
	}
    @Test
    public void testSaleItemsBySeller() {
        final User seller = newUser("user1");
        final SaleItem item = sellSaleItem(seller);

        Result res = executor(new ViewSaleItemsBySeller())
        		.param(ViewSaleItemsBySeller.InParam.SELLER_ID,
        				seller.getUserId())
        		.executeSuccess();

		List<SaleItem> itemList =
        	res.get(ViewSaleItemsBySeller.OutParam.SALE_ITEM_LIST);
		assertEquals(item, assertSingle(itemList));
	}

    @Test
    public void testSaleItem() {
        final User seller = newUser("user1");
        final SaleItem item = sellSaleItem(seller);

        Result res = executor(new ViewSaleItem())
        		.param(TransParam.SALE_ITEM_ID, item.getId())
        		.executeSuccess();

        assertEquals(item.getQnty(), res.get(TransParam.QNTY));
        assertEquals(seller, res.get(ViewSaleItem.OutParam.SELLER));
        assertEquals(item, res.get(ViewSaleItem.OutParam.SALE_ITEM));
    }
    @Test
    public void testSaleItemNoQty() {
        final User seller = newUser("user1");
        final SaleItem item = sellSaleItem(seller);
        item.setQnty(0);
        accessor().store(item);

        executor(new ViewSaleItem())
        .param(TransParam.SALE_ITEM_ID, item.getId())
        .executeSuccess(ResultMode.SALE_NO_QTY);
    }
    @Test
    public void testSaleItemEmpty() {
        final User seller = newUser("user1");
        ItemId iid = new ItemId(seller.getUserId(), 1);
    	Result res = executor(new ViewSaleItem())
        		.param(TransParam.SALE_ITEM_ID, iid)
        		.executeSuccess(ResultMode.EMPTY_RESULT);
        assertNull(res.get(ViewSaleItem.OutParam.SELLER));
        assertNull(res.get(ViewSaleItem.OutParam.SALE_ITEM));
    }
    @Test
    public void testWinningBidsByBidder() {
        final User seller = newUser("user1");
        final User bidder = newUser("user2");
        final AuctionItem item = sellItem(seller);
        final Bid bid = storeBid(bidder, item);
        item.setMaxBid(bid.getBidAmount());
        item.setEndDate(ParamUtil.dayBefore(1)); // closed
        accessor().store(item);
        { // still open
        	AuctionItem item2 = sellItem(seller);
        	Bid b = storeBid(bidder, item2);
            item2.setMaxBid(b.getBidAmount());
            item2.setEndDate(ParamUtil.dayAfter(1)); // open
            accessor().store(item2);
        }
        { // close and lost
        	AuctionItem item2 = sellItem(seller);
        	Bid b = storeBid(bidder, item2);
            item2.setMaxBid(b.getBidAmount() + 10);
            item.setEndDate(ParamUtil.dayBefore(1)); // closed
            accessor().store(item2);
        }

        Result res = executor(new ViewWinningBidsByBidder())
        		.param(SessionParam.USER_ID, bidder.getUserId())
        		.executeSuccess();

        /**
         * only one is winning.
         */
		List<AuctionItem> itemList = res.get(ViewWinningBidsByBidder.OutParam.WIN_ITEM_LIST);
		assertEquals(item, assertSingle(itemList));
		List<Bid> bidList = res.get(ViewWinningBidsByBidder.OutParam.WIN_BIDS);
		assertEquals(bid, assertSingle(bidList));
    }


}
