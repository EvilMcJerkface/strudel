package com.nec.strudel.bench.auction.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractSellSaleItem.InParam;
import com.nec.strudel.bench.auction.interactions.entity.SellSaleItem;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.impl.State;

@TestOn(SellSaleItem.class)
public class SellSaleItemTest extends AbstractInteractionTestBase {

    @Test
    public void testExecute() {
        final int sellerId = 1;
        for (int itemNo = 1; itemNo <= 10; itemNo++) {
            SaleItem item = 
            		expectedItem(sellerId, itemNo);

            executor()
            .param(SessionParam.USER_ID, sellerId)
            .param(InParam.NAME, item.getItemName())
            .param(InParam.PRICE, item.getPrice())
            .param(InParam.QNTY, item.getQnty())
            .executeSuccess();


            assertGetEquals(item, SaleItem.class, item.getId());

            List<SaleItem> itemList =
    				getList(SaleItem.class,
    						"sellerId",
    						sellerId);
            assertEquals(itemNo, itemList.size());
            assertTrue(itemList.contains(item));
        }
    }

    private SaleItem expectedItem(int sellerId, int itemNo) {
    	double price = 10;
        int qnty = 1;
        String itemName = "test-sale:" + itemNo;
        SaleItem item = new SaleItem(sellerId, itemNo);
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQnty(qnty);
        return item;
    }

	@Test
    public void testPrepare() {
        final int sellerId = 1;
        final int nameLen = 25;
    	// prepare state
		State state = newState()
    	.put(SessionParam.USER_ID, sellerId)
    	.put(SessionParam.SALE_PRICE_MIN, 0)
        .put(SessionParam.SALE_PRICE_MAX, 10)
        .put(SessionParam.SALE_QNTY_MIN, 0)
        .put(SessionParam.SALE_QNTY_MAX, 10)
        .put(SessionParam.ITEM_NAME_LEN, nameLen);
        // execute prepare
        Param param = prepare(state);
    	//Check prepare result
        assertNotNull(param.get(InParam.PRICE));
        assertNotNull(param.get(InParam.QNTY));
        String name = param.get(InParam.NAME);
        assertNotNull(name);
        assertEquals(nameLen, name.length());
        assertEquals(param.getInt(SessionParam.USER_ID), sellerId);
    }
}
