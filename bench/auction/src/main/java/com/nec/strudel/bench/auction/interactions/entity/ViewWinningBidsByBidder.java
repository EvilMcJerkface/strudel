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

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.BidId;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewWinningBidsByBidder;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewWinningBidsByBidder extends AbstractViewWinningBidsByBidder<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		int bidderId = getBidderId(param);

		List<Bid> winBids = new ArrayList<Bid>();
		List<AuctionItem> winItemList = new ArrayList<AuctionItem>();

		for (Bid bid : db.getEntitiesByIndex(
				Bid.class, "userId", bidderId)) {
			AuctionItem item = db.get(AuctionItem.class,
					bid.getAuctionItemId());
			if (item != null && isWinning(bid, item)) {
				winBids.add(bid);
				winItemList.add(item);
			}
		}
		return resultOf(winItemList, winBids, res);
	}

	public void executeWithTransactions(int bidderId, EntityDB db,
			List<Bid> winBids, List<AuctionItem> winItemList, ResultBuilder res) {
		for (BidId bidKey : db.scanIds(BidId.class,
				Bid.class, "userId", bidderId)) {
			EntityPair<AuctionItem, Bid> pair =
				db.run(Bid.class, bidKey,
					getAuctionItemBid(bidKey));

			AuctionItem auctionItem = pair.getFirst();
			Bid bid = pair.getSecond();

			if (auctionItem != null && bid != null
					&& isWinning(bid, auctionItem)) {
				winBids.add(bid);
				winItemList.add(auctionItem);
			}
		}
	}

	public EntityTask<EntityPair<AuctionItem, Bid>> getAuctionItemBid(
			final BidId bkey) {
        return new EntityTask<EntityPair<AuctionItem, Bid>>() {
            @Override
            public EntityPair<AuctionItem, Bid> run(EntityTransaction tx) {
				AuctionItem	auctionItem =
                    tx.get(AuctionItem.class, bkey.getItemId());
            	Bid bid = null;
				if (auctionItem != null) {
					bid = tx.get(Bid.class, bkey);
				}
				return EntityPair.of(auctionItem, bid);
            }
        };
    }
}
