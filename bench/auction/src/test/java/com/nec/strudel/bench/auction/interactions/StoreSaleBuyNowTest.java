package com.nec.strudel.bench.auction.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreSaleBuyNow.InParam;
import com.nec.strudel.bench.auction.interactions.entity.StoreSaleBuyNow;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.Executor;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.impl.State;

@TestOn(StoreSaleBuyNow.class)
public class StoreSaleBuyNowTest extends AbstractInteractionTestBase {

	@Test
    public void testExecute() {
		final int sellerId = 1;
		final int buyerId = 2;
		final int qnty = 12;
		final int qntyToBuy = 1;
		final SaleItem item = initItem(sellerId, qnty);
    	populate(item);

    	executor(buyerId, item.getId(), qntyToBuy).executeSuccess();

    	BuyNowSale bns = getSingle(BuyNowSale.class,
    			"itemId",
    			item.getId());
        assertSingle(bns, "buyerId", buyerId);
        assertGetEquals(bns, BuyNowSale.class, bns.getId());

        assertEquals(buyerId, bns.getBuyerId());
        assertEquals(qntyToBuy, bns.getQnty());

        SaleItem si = getNotNull(SaleItem.class, item.getId());
        assertEquals(item.getQnty() - bns.getQnty(), si.getQnty());
    }

    @Test
    public void testNoAvailableQnty() {
    	final int sellerId = 3;
    	final int buyerId = 2;
    	final int qnty = 1;
    	final int qntyToBuy = 2;

    	SaleItem item = initItem(sellerId, qnty);
    	populate(item);

    	executor(buyerId, item.getId(), qntyToBuy)
    	.executeFailure(ResultMode.SALE_NO_QTY);
    }
	@Test
    public void testPrepare() {
		final int sellerId = 2;
		final int buyerId = 3;
		final int qnty = 100;
		SaleItem item = initItem(sellerId, qnty);
    	// prepare state
		State state = newState()
    	.put(TransParam.SALE_ITEM_ID, item.getId())
    	.put(TransParam.QNTY, qnty)
    	.put(SessionParam.QNTY_ADJUSTER, 1)
        .put(SessionParam.USER_ID, buyerId);
        // execute prepare
        Param param = prepare(state);
    	//Check prepare result
        assertEquals(item.getId(),
        		param.getObject(TransParam.SALE_ITEM_ID));
        assertEquals(buyerId,
        		param.getInt(SessionParam.USER_ID));

        assertNotNull(param.get(InParam.QNTY_TO_BUY));
        assertTrue(0 < param.getInt(InParam.QNTY_TO_BUY));
        assertTrue(param.getInt(InParam.QNTY_TO_BUY) <= qnty);

        assertNotNull(param.get(InParam.BNS_DATE));
    }

	/**
	 * If QNTY = 1, QNTY_TO_BUY must be 1
	 */
	@Test
    public void testPrepareQnty1() {
		final int sellerId = 2;
		final int buyerId = 3;
		final int qnty = 1;
		SaleItem item = initItem(sellerId, qnty);
    	// prepare state
		State state = newState()
    	.put(TransParam.SALE_ITEM_ID, item.getId())
    	.put(TransParam.QNTY, qnty)
    	.put(SessionParam.QNTY_ADJUSTER, 1)
        .put(SessionParam.USER_ID, buyerId);
        // execute prepare
        Param param = prepare(state);

        assertEquals(1, param.getInt(InParam.QNTY_TO_BUY));
    }

    private SaleItem initItem(int sellerId, int qnty) {
    	int itemNo = 1;
    	final int price = 100;
    	SaleItem item = new SaleItem(sellerId, itemNo);
    	item.setItemName("name");
    	item.setPrice(price);
    	item.setQnty(qnty);
    	return item;
    }

    private Executor<?> executor(int buyerId, ItemId iid, int qntyToBuy) {
    	return executor()
    			.param(TransParam.SALE_ITEM_ID, iid)
    			.param(InParam.QNTY_TO_BUY, qntyToBuy)
    			.param(InParam.BNS_DATE, ParamUtil.now())
    			.param(SessionParam.USER_ID, buyerId);
    }
}
