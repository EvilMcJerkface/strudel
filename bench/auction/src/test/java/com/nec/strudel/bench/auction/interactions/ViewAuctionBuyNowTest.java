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
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionBuyNow.OutParam;
import com.nec.strudel.bench.auction.interactions.entity.ViewAuctionBuyNow;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(ViewAuctionBuyNow.class)
public class ViewAuctionBuyNowTest extends AbstractInteractionTestBase {


    @Test
    public void testExecute() {
        User user = new User(1, "u1");

        ItemId itemId = new ItemId(user.getUserId(), 2);
        BuyNowAuction bna = new BuyNowAuction(itemId);
        bna.setBuyerId(user.getUserId());
        bna.setBnaDate(new Date().getTime());

        populate(user, bna);

        Result res = executor()
        		.param(TransParam.AUCTION_ITEM_ID, itemId)
        		.executeSuccess();

        BuyNowAuction bna1 = res.get(OutParam.BNA);
        assertNotNull(bna1);
        User user1 = res.get(OutParam.BUYER);
        assertEquals(user.getUname(), user1.getUname());

    }
	@Test
    public void testPrepare() {
		final ItemId id = new ItemId(1, 2);
    	// prepare state
		State state = newState()
    	.put(TransParam.AUCTION_ITEM_ID, id);
    	// execute prepare
    	Param param = prepare(state);
    	//Check prepare result
        assertEquals(id,
        		param.getObject(TransParam.AUCTION_ITEM_ID));
    }
	Param param(ItemId id) {
        Param p = new Param();
        p.put(TransParam.AUCTION_ITEM_ID, id);
        return p;
    }
}
