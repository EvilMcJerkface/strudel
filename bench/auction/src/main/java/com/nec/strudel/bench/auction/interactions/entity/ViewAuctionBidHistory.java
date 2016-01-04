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

public class ViewAuctionBidHistory extends AbstractViewAuctionBidHistory<EntityDB>
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
