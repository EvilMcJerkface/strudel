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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleItemsBySeller.InParam;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleItemsBySeller.OutParam;
import com.nec.strudel.bench.auction.interactions.entity.ViewSaleItemsBySeller;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.impl.State;

@TestOn(ViewSaleItemsBySeller.class)
public class ViewSaleItemsBySellerTest extends AbstractInteractionTestBase {
    @Test
    public void testExecute() {
        final int sellerId = 1;
        final User seller = new User(sellerId, "user1");
        final SaleItem item = createItem(seller);

        populate(seller, item);

        Result res = executor()
        		.param(InParam.SELLER_ID, seller.getUserId())
        		.executeSuccess();

		List<SaleItem> itemList =
        	res.get(OutParam.SALE_ITEM_LIST);
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

    	Param param = prepare(state);

        assertNotNull(param.getObject(InParam.SELLER_ID));
        assertNotSame(userId, param.getInt(InParam.SELLER_ID));
    }
	@Test
    public void testComplete() {
		int sellerId = 1;
		int itemNo = 1;
		SaleItem item = new SaleItem(sellerId, itemNo);
		ResultBuilder res = new ResultBuilder();
		// prepare res
    	List<SaleItem> itemList = new ArrayList<SaleItem>();
    	itemList.add(item);
    	res.set(OutParam.SALE_ITEM_LIST, itemList);
    	// execute complete
    	State state = complete(res.success());
    	//Check complete result
    	assertNotNull(state.get(TransParam.SALE_ITEM_ID));
    }

	private SaleItem createItem(User seller) {
        SaleItem item = new SaleItem(seller.getUserId());
        initItem(item);
        return item;
	}
    private void initItem(SaleItem item) {
        final double price = 10;
        final int qnty = 1;
    	item.setItemName("name");
    	item.setPrice(price);
    	item.setQnty(qnty);
    }

}
