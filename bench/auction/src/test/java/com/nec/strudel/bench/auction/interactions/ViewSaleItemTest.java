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
package com.nec.strudel.bench.auction.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleItem.OutParam;
import com.nec.strudel.bench.auction.interactions.entity.ViewSaleItem;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.impl.State;

@TestOn(ViewSaleItem.class)
public class ViewSaleItemTest extends AbstractInteractionTestBase {

    @Test
    public void testExecute() {
        final User seller = new User(1, "user1");
        final SaleItem item = initItem(seller);

        populate(seller, item);

        Result res = executor()
        		.param(TransParam.SALE_ITEM_ID, item.getId())
        		.executeSuccess();

        assertEquals(item.getQnty(), res.get(TransParam.QNTY));
        assertEquals(seller, res.get(OutParam.SELLER));
        assertEquals(item, res.get(OutParam.SALE_ITEM));
    }
    @Test
    public void testExecuteNoQty() {
        final User seller = new User(1, "user1");
        final SaleItem item = initItem(seller);
        item.setQnty(0);

        populate(seller, item);

        executor()
        .param(TransParam.SALE_ITEM_ID, item.getId())
        .executeSuccess(ResultMode.SALE_NO_QTY);
    }
    @Test
    public void testExecuteEmpty() {

    	Result res = executor()
        		.param(TransParam.SALE_ITEM_ID, new ItemId(1, 1))
        		.executeSuccess(ResultMode.EMPTY_RESULT);
        assertNull(res.get(OutParam.SELLER));
        assertNull(res.get(OutParam.SALE_ITEM));
    }
    
	@Test
    public void testPrepare() {
        final User seller = new User(2, "user2");
		SaleItem item = initItem(seller);
    	// prepare state
		State state = newState()
    	.put(TransParam.SALE_ITEM_ID, item.getId());
    	// execute prepare
    	Param param = prepare(state);
    	// Check prepare result
    	assertEquals(item.getId(),
        		param.getObject(TransParam.SALE_ITEM_ID));
    }
	@Test
    public void testComplete() {
        final int qnty = 10;
		ResultBuilder res = new ResultBuilder();
		// prepare res
    	res.set(TransParam.QNTY, qnty);
    	// execute complete
    	State state = complete(res.success());
    	// Check complete result
    	assertEquals(qnty,
    			state.getInt(TransParam.QNTY));
    }
    private SaleItem initItem(User seller) {
        final int itemNo = 1;
        final double price = 10;
        final int qnty = 1;
    	SaleItem item = new SaleItem(seller.getUserId(), itemNo);
    	item.setItemName("name");
    	item.setPrice(price);
    	item.setQnty(qnty);
    	return item;
    }

}
