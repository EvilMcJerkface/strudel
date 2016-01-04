package com.nec.strudel.bench.auction;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.nec.strudel.bench.auction.interactions.InteractionTestSuite;
import com.nec.strudel.bench.auction.populate.PopulateTestSuite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
    InteractionTestSuite.class,
    PopulateTestSuite.class,
})
public class AuctionTestSuite {

}
