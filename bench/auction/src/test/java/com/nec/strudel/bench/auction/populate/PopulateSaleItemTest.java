package com.nec.strudel.bench.auction.populate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.populate.SaleParamSet;
import com.nec.strudel.bench.auction.populate.SaleParamSet.SaleParam;
import com.nec.strudel.bench.auction.populate.entity.PopulateSaleItem;
import com.nec.strudel.bench.test.EntityAssert;
import com.nec.strudel.bench.test.populate.AbstractPopulateTestBase;
import com.nec.strudel.bench.test.populate.TestOn;

@TestOn(PopulateSaleItem.class)
public class PopulateSaleItemTest extends AbstractPopulateTestBase<SaleParamSet> {
	static final int USER_NUM = 2;  // fix to 2
	static final int BUYER_ID = 2;
	static final int SELLER_ID = 1;

	SaleItemParams param() {
		return new SaleItemParams(SELLER_ID, USER_NUM);
	}
	@Test
    public void testPopulate() {
    	SaleParamSet sps = param().create();
    	process(sps);
    	checkConsistencyWithParams(sps);
    }
    /**
     * different number of items per user.
     */
    @Test
    public void testPopulateWithTestParamMoreItems() {
    	SaleParamSet sps = 
    			param().setItemsPerUser(10).create();
    	process(sps);
    	checkConsistencyWithParams(sps);
    }
    /**
     * different number of BuyNowSale per item.
     */
    @Test
    public void testPopulateWithTestParamMoreBNSs() {
    	SaleParamSet sps = param()
    			.setNumOfBns(10)
    			.create();
    	process(sps);
    	checkConsistencyWithParams(sps);
    }
    /**
     * Control that no BNS is created for items.
     */
    @Test
    public void testPopulateWithTestParamNoBNS() {
    	SaleParamSet sps = param()
    			.setBuynowRatio(0)
    			.create();
    	process(sps);
    	checkConsistencyWithParams(sps);

    	List<SaleItem> expected = expectedSaleItems(sps);
        for (SaleItem item : expected) {
        	assertEmpty(BuyNowSale.class, "itemId",
    				item.getId());
        }
    	assertEmpty(BuyNowSale.class, "buyerId",
    			BUYER_ID);
    }

    private void checkConsistencyWithParams(SaleParamSet sps) {
        List<SaleItem> expected = expectedSaleItems(sps);
        int idx = 0;
        Set<BuyNowSale> allBNSs = new HashSet<BuyNowSale>();
        for (SaleItem item : expected) {
        	ItemId iid = item.getId();
	    	assertGetEquals(item, SaleItem.class, iid);

	    	assertIndexed(item, "sellerId");

        	List<BuyNowSale> expectedBNSs =
        			expectedBNSs(iid, sps.getParams()[idx]);
        	allBNSs.addAll(expectedBNSs);
        	for (BuyNowSale bns : expectedBNSs) {
        		assertGetEquals(bns, BuyNowSale.class, bns.getId());
        	}
            checkSaleBuyNowIndex(iid, expectedBNSs);
	        idx++;
        }
        checkBuyerSaleIndex(BUYER_ID, allBNSs);
    }

    public List<SaleItem> expectedSaleItems(SaleParamSet sps) {
    	SaleParam[] ps = sps.getParams();
    	List<SaleItem> items = new ArrayList<SaleItem>();
    	for (int i = 0; i < ps.length; i++) {
	    	SaleItem item = new SaleItem(sps.sellerId(), i + 1);
	    	ps[i].build(item);
	        items.add(item);
    	}
        return items;
    }
    public List<BuyNowSale> expectedBNSs(ItemId iid, SaleParam sp) {
    	List<BuyNowSale> bnss = new ArrayList<BuyNowSale>();
    	for (int i = 1; i <= sp.numOfBns(); i++) {
    		bnss.add(new BuyNowSale(iid, i));
    	}
    	sp.build(bnss);
    	return bnss;
    }

    public void checkSaleBuyNowIndex(ItemId iid, List<BuyNowSale> expectedBNSs) {
    	EntityAssert.assertSameEntitySets(expectedBNSs,
    			getList(BuyNowSale.class, "itemId", iid));
    }
    public void checkBuyerSaleIndex(int buyerId, Set<BuyNowSale> expectedBNSs) {
    	EntityAssert.assertSameEntitySets(expectedBNSs,
    			getList(BuyNowSale.class, "buyerId", buyerId));
    }
}
