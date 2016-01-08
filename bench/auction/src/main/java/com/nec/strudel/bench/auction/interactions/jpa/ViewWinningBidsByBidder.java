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
package com.nec.strudel.bench.auction.interactions.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewWinningBidsByBidder;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewWinningBidsByBidder extends AbstractViewWinningBidsByBidder<EntityManager>
implements Interaction<EntityManager>  {
	public static final String Q_BIDS =
			"SELECT b FROM Bid b WHERE b.userId = :uid";
	public static final String Q_BIDS_ITEM =
			"SELECT b,a FROM Bid b, AuctionItem a WHERE b.userId = :uid"
			+ " AND b.sellerId = a.sellerId AND b.itemNo = a.itemNo"
			+ " AND b.bidAmount = a.maxBid";
	public static final String P_BIDDER_ID = "uid";
	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int bidderId = getBidderId(param);

		List<Bid> winBids = new ArrayList<Bid>();
		List<AuctionItem> winItemList = new ArrayList<AuctionItem>();
		for (Object o : em.createQuery(Q_BIDS_ITEM)
				.setParameter(P_BIDDER_ID, bidderId)
				.getResultList()) {
			Object[] tuple = (Object[]) o;
			Bid bid = (Bid) tuple[0];
			AuctionItem item = (AuctionItem) tuple[1];
			if (isWinning(bid, item)) {
				winBids.add(bid);
				winItemList.add(item);
			}
		}
		return resultOf(winItemList, winBids, res);
	}
	public Result executeByFind(int bidderId, EntityManager em, ResultBuilder res) {
		List<Bid> winBids = new ArrayList<Bid>();
		List<AuctionItem> winItemList = new ArrayList<AuctionItem>();

		List<Bid> bids = em.createQuery(Q_BIDS, Bid.class)
				.setParameter(P_BIDDER_ID, bidderId)
				.getResultList();
		for (Bid b : bids) {
			AuctionItem item = em.find(AuctionItem.class,
					b.getAuctionItemId());
			if (item != null && isWinning(b, item)) {
				winBids.add(b);
				winItemList.add(item);
			}
		}
		return resultOf(winItemList, winBids, res);
	}

}
