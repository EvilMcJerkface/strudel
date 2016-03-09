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

package com.nec.strudel.bench.auction.interactions.entity;

import java.util.ArrayList;
import java.util.List;

import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionBidHistory;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewAuctionBidHistory
        extends AbstractViewAuctionBidHistory<EntityDB>
        implements Interaction<EntityDB> {
    @Override
    public Result execute(Param param, EntityDB db, ResultBuilder res) {
        ItemId itemId = getItemId(param);

        List<Bid> bids = db.getEntitiesByIndex(Bid.class,
                "auctionItemId",
                itemId);

        List<User> bidders = new ArrayList<User>();
        for (Bid bid : bids) {
            User bidder = db.get(User.class,
                    bid.getUserId());
            if (bidder != null) {
                bidders.add(bidder);
            }
        }
        return resultOf(bids, bidders, res);
    }
}
