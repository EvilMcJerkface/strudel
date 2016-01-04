package com.nec.strudel.bench.auction.interactions.entity;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreAuctionBuyNow;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

/**
 * Tries to buy the current auction item of interest,
 * which is specified by AUCTION_ITEM_ID.
 * The trial fails if the auction item has been sold
 * (AUCTION_SOLD)
 * or expired (AUCTION_EXPIRED).
 *
 */
public class StoreAuctionBuyNow extends AbstractStoreAuctionBuyNow<EntityDB>
implements Interaction<EntityDB> {

	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {

		BuyNowAuction bna = createBna(param);

		return db.run(bna, update(bna, res));
	}

	protected EntityTask<Result> update(final BuyNowAuction bna, final ResultBuilder res) {
		return new EntityTask<Result>() {
            @Override
            public Result run(EntityTransaction tx) {
                AuctionItem item =
                		tx.get(AuctionItem.class, bna.getItemId());
                Result r = check(bna, item, res);
                if (r.isSuccess()) {
                	tx.create(bna);
                	AuctionItem.sell(item);
                	tx.update(item);
                }
                return r;
            }
		};
	}



}
