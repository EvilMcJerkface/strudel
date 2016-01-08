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

import java.util.List;

import org.junit.Test;

import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionBidHistory.OutParam;
import com.nec.strudel.bench.auction.interactions.entity.ViewAuctionBidHistory;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.test.interactions.AbstractInteractionTestBase;
import com.nec.strudel.bench.test.interactions.TestOn;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.impl.State;

@TestOn(ViewAuctionBidHistory.class)
public class ViewAuctionBidHistoryTest extends AbstractInteractionTestBase {

    @Test
    public void testExecute() {
        final User user = new User(1, "u1");
        final ItemId iid = new ItemId(2, 1);
        Bid bid = new Bid(iid);
        bid.setUserId(user.getUserId());

        populate(user, bid);

        Result res = executor()
        		.param(TransParam.AUCTION_ITEM_ID, iid)
        		.executeSuccess();

		List<Bid> bidList = res.get(OutParam.BID_LIST);
		assertSingle(bid, bidList);

		List<User> bidderList = res.get(OutParam.BIDDER_LIST);
		assertSingle(user, bidderList);
    }
	@Test
    public void testPrepare() {
        final ItemId iid = new ItemId(2, 1);
		State state = newState()
    	.put(TransParam.AUCTION_ITEM_ID, iid);

    	Param param = prepare(state);

    	assertEquals(iid,
        		param.getObject(TransParam.AUCTION_ITEM_ID));
    }

}
