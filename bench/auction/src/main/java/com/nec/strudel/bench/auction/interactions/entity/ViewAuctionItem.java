package com.nec.strudel.bench.auction.interactions.entity;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionItem;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;


public class ViewAuctionItem extends AbstractViewAuctionItem<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		ItemId itemId = getItemId(param);
		if (itemId == null) {
			return res.warn("AUCTION_ITEM_ID not specified"
					+ " in the parameter: " + param)
					.failure(ResultMode.MISSING_PARAM);
		}

		User seller = db.get(User.class, itemId.getSellerId());
		EntityPair<AuctionItem, BuyNowAuction> pair =
				db.run(AuctionItem.class, itemId,
					getAuctionItemBNA(itemId));

		AuctionItem item = pair.getFirst();
		// If the item is sold by BUY NOW,
		// get corresponding Buyer Info
		BuyNowAuction bna = pair.getSecond();
		User buyer = null;
		if (bna != null) {
			int buyerId = bna.getBuyerId();
			buyer = db.get(User.class, buyerId);
		}

		return resultOf(item, seller, bna, buyer, param, res);
	}

	public EntityTask<EntityPair<AuctionItem, BuyNowAuction>>
				getAuctionItemBNA(final ItemId itemKey) {
        return new EntityTask<EntityPair<AuctionItem, BuyNowAuction>>() {
            @Override
            public EntityPair<AuctionItem, BuyNowAuction>
            run(EntityTransaction tx) {
            	BuyNowAuction bna = null;
    			AuctionItem auctionItem =
                     tx.get(AuctionItem.class, itemKey);
    			if (auctionItem != null
    					&& AuctionItem.isSold(auctionItem)) {
    					bna = tx.get(BuyNowAuction.class,
    							itemKey);
    			}
    			return EntityPair.of(auctionItem, bna);
            }
        };
    }
}
