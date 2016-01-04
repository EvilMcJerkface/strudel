package com.nec.strudel.bench.auction.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionBuyNow;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewAuctionBuyNow extends AbstractViewAuctionBuyNow<EntityManager>
implements Interaction<EntityManager> {
	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		ItemId itemId = getItemId(param);
		if (itemId == null) {
			return res.warn("AUCTION_ITEM_ID not specified"
					+ " in the parameter: " + param)
					.failure(ResultMode.MISSING_PARAM);
		}

		BuyNowAuction bna = em.find(BuyNowAuction.class, itemId);
		User buyer = null;
		if (bna != null) {
			buyer = em.find(User.class, bna.getBuyerId());
		}
		return resultOf(bna, buyer, param, res);
	}

}
