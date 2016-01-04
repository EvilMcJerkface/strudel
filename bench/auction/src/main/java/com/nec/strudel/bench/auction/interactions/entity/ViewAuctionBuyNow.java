package com.nec.strudel.bench.auction.interactions.entity;

import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionBuyNow;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewAuctionBuyNow extends AbstractViewAuctionBuyNow<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		ItemId itemId = getItemId(param);
		if (itemId == null) {
			return res.warn("AUCTION_ITEM_ID not specified"
					+ " in the parameter: " + param)
					.failure(ResultMode.MISSING_PARAM);
		}

		BuyNowAuction bna = db.get(BuyNowAuction.class, itemId);
		User buyer = null;
		if (bna != null) {
			buyer = db.get(User.class, bna.getBuyerId());
		}
		return resultOf(bna, buyer, param, res);
	}

}
