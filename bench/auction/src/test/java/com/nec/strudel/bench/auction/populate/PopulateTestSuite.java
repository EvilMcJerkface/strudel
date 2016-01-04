package com.nec.strudel.bench.auction.populate;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
    PopulateUserTest.class,
    PopulateSaleItemTest.class,
    PopulateAuctionItemTest.class,
})
public class PopulateTestSuite {

}
