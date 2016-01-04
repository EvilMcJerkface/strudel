package com.nec.strudel.bench.auction.interactions.jpa;

import static org.junit.Assert.*;

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
import com.nec.strudel.bench.auction.interactions.base.AbstractSellAuctionItem.InParam;
import com.nec.strudel.bench.auction.interactions.jpa.SellAuctionItem;
import com.nec.strudel.bench.auction.interactions.jpa.SellSaleItem;
import com.nec.strudel.bench.auction.interactions.jpa.StoreAuctionBuyNow;
import com.nec.strudel.bench.auction.interactions.jpa.StoreBid;
import com.nec.strudel.bench.auction.interactions.jpa.StoreSaleBuyNow;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.bench.test.JpaDatabase;
import com.nec.strudel.bench.test.interactions.Executor;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Result;

public class StoreInteractionsTest {
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
	protected User newUser() {
		int userId =  USER_IDS.getAndIncrement();
		User user = new User(userId, "u" + userId);
		accessor().store(user);
		return user;
	}
	public Executor<EntityManager> executor(Interaction<EntityManager> interaction) {
		return new Executor<EntityManager>(interaction, em);
	}
	public AuctionAccessor accessor() {
		return new AuctionAccessor(em);
	}

	Result sellAuctionItem(AuctionItem item) {
		return executor(new SellAuctionItem())
        .param(InParam.NAME, item.getItemName())
		.param(InParam.INITIAL_BID, item.getInitialBid())
		.param(InParam.BUYNOW, item.getBuyNow())
		.param(InParam.MAX_BID, item.getMaxBid())
		.param(InParam.AUCTION_END_DATE, item.getEndDate())
		.param(SessionParam.USER_ID, item.getSellerId())
		.executeSuccess();
	}
	Result sellSaleItem(SaleItem item) {
		return executor(new SellSaleItem())
		.param(SessionParam.USER_ID, item.getSellerId())
		.param(SellSaleItem.InParam.NAME,
						item.getItemName())
		.param(SellSaleItem.InParam.PRICE,
						item.getPrice())
		.param(SellSaleItem.InParam.QNTY,
						item.getQnty())
		.executeSuccess();

	}
	private Executor<?> storeBid(int bidderId, AuctionItem item, double bidAmount) {
		return executor(new StoreBid())
				.param(TransParam.AUCTION_ITEM_ID,
						item.getItemId())
						.param(TransParam.MAX_BID, item.getMaxBid())
						.param(StoreBid.InParam.BID_DATE, ParamUtil.now())
						.param(StoreBid.InParam.BID_AMOUNT, bidAmount)
						.param(SessionParam.USER_ID,
								bidderId);	
	}
	private Executor<?> storeAuctionBuyNow(int buyerId, AuctionItem item) {
		return executor(new StoreAuctionBuyNow())
				.param(TransParam.AUCTION_ITEM_ID,
						item.getItemId())
				.param(StoreAuctionBuyNow.InParam.BNA_DATE,
								ParamUtil.now())
				.param(SessionParam.USER_ID, buyerId);
	}
    private Executor<?> storeSaleBuyNow(int buyerId, ItemId iid, int qntyToBuy) {
    	return executor(new StoreSaleBuyNow())
    			.param(TransParam.SALE_ITEM_ID, iid)
    			.param(StoreSaleBuyNow.InParam.QNTY_TO_BUY, qntyToBuy)
    			.param(StoreSaleBuyNow.InParam.BNS_DATE, ParamUtil.now())
    			.param(SessionParam.USER_ID, buyerId);
    }
	@Test
	public void sellAuctionItemTest() {
		AuctionItem item = newItem();
		int sellerId = item.getSellerId();
		sellAuctionItem(item);

		AuctionItem stored = assertSingle(
				accessor().auctionsBySeller(sellerId));
		/**
		 * import dynamically generated key:
		 */
		item.setItemNo(stored.getItemNo());
		/**
		 * the rest should be the same
		 */
		assertEquals(item, stored);
		
	}
	@Test
	public void sellSaleItemTest() {
		SaleItem item = newSaleItem(1);
		int sellerId = item.getSellerId();
		sellSaleItem(item);
		SaleItem stored = assertSingle(
				accessor().salesBySeller(sellerId));
		/**
		 * import dynamically generated key:
		 */
		item.setItemNo(stored.getItemNo());
		/**
		 * the rest should be the same
		 */
		assertEquals(item, stored);
	}
	@Test
    public void testBid() {
		final int bidderId = newUser().getUserId();
    	AuctionItem item = newItem();
    	AuctionAccessor acc = accessor();
    	acc.store(item);
    	item = assertSingle(acc.auctionsBySeller(item.getSellerId()));

    	double bidAmount = item.getMaxBid() * 1.5;

    	storeBid(bidderId, item, bidAmount).executeSuccess();

    	Bid bid = assertSingle(acc.bidsByItem(item.getItemId()));
    	assertEquals(bidderId, bid.getUserId());
 
    	assertEquals(bidAmount, bid.getBidAmount(), 0);

    	AuctionItem updated =
    			assertSingle(acc.auctionsBySeller(item.getSellerId()));

        assertEquals(bidAmount, updated.getMaxBid(), 0.1);
    }
    @Test
    public void testBidAmountLessThanMaxBid() {
		final int bidderId = newUser().getUserId();
    	AuctionItem item = newItem();
    	AuctionAccessor acc = accessor();
    	acc.store(item);
    	item = assertSingle(acc.auctionsBySeller(item.getSellerId()));
        double bidAmount = item.getMaxBid() / 2;

       storeBid(bidderId, item, bidAmount)
        .executeFailure(ResultMode.LOSING_BID);

      	AuctionItem afterItem =
    			assertSingle(acc.auctionsBySeller(item.getSellerId()));
      	assertEquals(item, afterItem);
    }
    @Test
    public void testBidOnExpired() {
		final int bidderId = newUser().getUserId();
    	AuctionItem item = newItem();
    	item.setEndDate(ParamUtil.dayBefore(1));
    	AuctionAccessor acc = accessor();
    	acc.store(item);
    	item = assertSingle(acc.auctionsBySeller(item.getSellerId()));
    	storeBid(bidderId, item, item.getMaxBid() * 2)
    	.executeFailure(ResultMode.AUCTION_EXPIRED);

      	AuctionItem afterItem =
    			assertSingle(acc.auctionsBySeller(item.getSellerId()));
      	assertEquals(item, afterItem);
    }
    @Test
    public void testBidOnSold() {
		final int bidderId = newUser().getUserId();
    	AuctionItem item = newItem();
    	AuctionItem.sell(item);
    	AuctionAccessor acc = accessor();
    	acc.store(item);
    	item = assertSingle(acc.auctionsBySeller(item.getSellerId()));
    	storeBid(bidderId, item, item.getMaxBid() * 2)
    	.executeFailure(ResultMode.AUCTION_SOLD);

      	AuctionItem afterItem =
    			assertSingle(acc.auctionsBySeller(item.getSellerId()));
      	assertEquals(item, afterItem);
    }
    @Test
    public void testAuctionBuyNow() {
		final int buyerId = newUser().getUserId();
    	AuctionItem item = newItem();
    	AuctionAccessor acc = accessor();
    	acc.store(item);
    	item = assertSingle(acc.auctionsBySeller(item.getSellerId()));

    	storeAuctionBuyNow(buyerId, item).executeSuccess();

    	BuyNowAuction bna = 
    			assertSingle(acc.bnaByBuyer(buyerId));
		assertEquals(item.getItemId(), bna.getItemId());
		assertEquals(buyerId, bna.getBuyerId());

		assertEquals(bna, acc.bna(item.getItemId()));

		AuctionItem updated = acc.item(item.getItemId());
    	assertTrue(AuctionItem.isSold(updated));
    }
    @Test
    public void testBuyOnExpired() {
		final int buyerId = newUser().getUserId();
    	AuctionItem item = newItem();
    	AuctionAccessor acc = accessor();
    	item.setEndDate(ParamUtil.dayBefore(1));
    	acc.store(item);
    	item = assertSingle(acc.auctionsBySeller(item.getSellerId()));
    	storeAuctionBuyNow(buyerId, item)
    	.executeFailure(ResultMode.AUCTION_EXPIRED);
    }
    @Test
    public void testBuyOnSold() {
		final int buyerId = newUser().getUserId();
    	AuctionItem item = newItem();
    	AuctionAccessor acc = accessor();
    	AuctionItem.sell(item);
    	acc.store(item);
    	item = assertSingle(acc.auctionsBySeller(item.getSellerId()));
    	storeAuctionBuyNow(buyerId, item)
    	.executeFailure(ResultMode.AUCTION_SOLD);
    }
	@Test
    public void testSaleBuyNow() {
		final int buyerId = newUser().getUserId();
		final int qnty = 12;
		final int qntyToBuy = 1;
		SaleItem item = newSaleItem(qnty);
    	AuctionAccessor acc = accessor();
    	acc.store(item);
    	item = assertSingle(acc.salesBySeller(item.getSellerId()));

    	storeSaleBuyNow(buyerId, item.getId(), qntyToBuy).executeSuccess();

    	BuyNowSale bns = 
    			assertSingle(acc.bnsByItem(item.getId()));
    	BuyNowSale bns1 =
    			assertSingle(acc.bnsByBuyer(buyerId));
    	assertEquals(bns, bns1);
    	assertEquals(bns, acc.bns(bns.getId()));

        assertEquals(buyerId, bns.getBuyerId());
        assertEquals(qntyToBuy, bns.getQnty());

        SaleItem si = acc.sale(item.getId());
        assertEquals(qnty - qntyToBuy, si.getQnty());
    }
    @Test
    public void testNoAvailableQnty() {
    	final int buyerId = newUser().getUserId();
    	final int qnty = 1;
    	final int qntyToBuy = 2;

    	SaleItem item = newSaleItem(qnty);
    	AuctionAccessor acc = accessor();
    	acc.store(item);
    	item = assertSingle(acc.salesBySeller(item.getSellerId()));

    	storeSaleBuyNow(buyerId, item.getId(), qntyToBuy)
    	.executeFailure(ResultMode.SALE_NO_QTY);
    }

	protected <T> T assertSingle(List<T> items) {
		assertEquals(1, items.size());
		return items.get(0);
	}

	private AuctionItem newItem() {
        final int sellerId = newUser().getUserId();
    	final double maxBid = 10;
    	final long endDate = ParamUtil.dayAfter(1);

    	AuctionItem item = new AuctionItem(sellerId);
    	item.setBuyNow(200);
    	item.setEndDate(endDate);
    	item.setInitialBid(1);
    	item.setItemName("name");
        item.setMaxBid(maxBid);
        return item;
    }
    private SaleItem newSaleItem(int qnty) {
        final int sellerId = newUser().getUserId();
    	final int price = 100;
    	SaleItem item = new SaleItem(sellerId);
    	item.setItemName("name");
    	item.setPrice(price);
    	item.setQnty(qnty);
    	return item;
    }

}
