package com.nec.strudel.bench.auction.interactions.entity;

import java.util.ArrayList;
import java.util.List;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.BidId;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewBidsByBidder;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewBidsByBidder extends AbstractViewBidsByBidder<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		int bidderId = getBidderId(param);

		List<AuctionItem> itemList = new ArrayList<AuctionItem>();
		List<Bid> bidList =
				db.getEntitiesByIndex(Bid.class, "userId", bidderId);
		for (Bid bid : bidList) {
			AuctionItem item = db.get(AuctionItem.class,
					bid.getAuctionItemId());
			if (item != null) {
				itemList.add(item);
			} else {
				res.warn("auction item (" + bid.getAuctionItemId()
					+ ") not found for bid=" + bid.getId());
			}
		}

		return resultOf(itemList, bidList, res);
	}

	public void executeWithTransactions(int bidderId, EntityDB db,
			List<AuctionItem> itemList, List<Bid> bidList, ResultBuilder res) {
		for (BidId bidKey : db.scanIds(BidId.class, Bid.class,
				"userId", bidderId)) {
			EntityPair<AuctionItem, Bid> pair =
				db.run(Bid.class, bidKey,
					getAuctionItemBid(bidKey));

			AuctionItem auctionItem = pair.getFirst();
			Bid bid = pair.getSecond();

			if (auctionItem != null) {
				itemList.add(auctionItem);
			} else {
				res.warn("auction item (" + bidKey.getItemId()
					+ ") not found for bid=" + bidKey);
			}
			if (bid != null) {
				bidList.add(bid);
			} else {
				res.warn("bid in bidder_bid_idx not found: bid="
						+ bidKey);
			}
		}
	}

	public EntityTask<EntityPair<AuctionItem, Bid>> getAuctionItemBid(
			final BidId bidKey) {
        return new EntityTask<EntityPair<AuctionItem, Bid>>() {
            @Override
            public EntityPair<AuctionItem, Bid> run(EntityTransaction tx) {
            	Bid bid = null;
            	AuctionItem auctionItem =
                    tx.get(AuctionItem.class, bidKey.getItemId());
            	if (auctionItem != null) {
            		bid = tx.get(Bid.class, bidKey);
            	}
            	return EntityPair.of(auctionItem, bid);
            }
        };
    }
}
