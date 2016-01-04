package com.nec.strudel.bench.auction.interactions;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
    SellAuctionItemTest.class,
    SellSaleItemTest.class,
    StoreAuctionBuyNowTest.class,
    StoreBidTest.class,
    StoreSaleBuyNowTest.class,
    ViewAuctionBidHistoryTest.class,
    ViewAuctionBuyNowTest.class,
    ViewAuctionItemTest.class,
    ViewAuctionItemsByBuyerTest.class,
    ViewAuctionItemsBySellerTest.class,
    ViewBidsByBidderTest.class,
    ViewSaleBuyNowHistoryTest.class,
    ViewSaleItemTest.class,
    ViewSaleItemsByBuyerTest.class,
    ViewSaleItemsBySellerTest.class,
    ViewUserTest.class,
    ViewWinningBidsByBidderTest.class,
})
public class InteractionTestSuite {

}
