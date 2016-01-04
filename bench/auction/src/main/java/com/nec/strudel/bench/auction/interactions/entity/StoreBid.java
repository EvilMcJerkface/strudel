package com.nec.strudel.bench.auction.interactions.entity;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreBid;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

/**
 * Tries to place a bid on the current auction item of
 * the user's interest, which is specified by AUCTION_ITEM_ID.
 * The trial can fail with the following mode:
 * <ul>
 * <li> AUCTION_SOLD: the auction item has been sold.
 * <li> AUCTION_EXPIRED: the auction item has been expired.
 * <li> LOSING_BID: the bidding price is not
 * higher than the current maximum price.
 * </ul>
 *
 */
public class StoreBid extends AbstractStoreBid<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {

		Bid bid = createBid(param);
		return db.run(bid, update(bid, res));
	}

	public EntityTask<Result> update(
			final Bid bid, final ResultBuilder res) {
		return new EntityTask<Result>() {
			@Override
			public Result run(EntityTransaction tx) {
				AuctionItem item =
	                tx.get(AuctionItem.class, bid.getAuctionItemId());
				Result r = check(bid, item, res);
				if (r.isSuccess()) {
					tx.create(bid);
	                item.setMaxBid(bid.getBidAmount());
					tx.update(item);
				}
				return r;
			}
		};
	}

}
