package com.nec.strudel.bench.auction.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionItem;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewAuctionItem extends AbstractViewAuctionItem<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		ItemId itemId = getItemId(param);
		User seller = em.find(User.class, itemId.getSellerId());

		BuyNowAuction bna = null;
		User buyer = null;
		/**
		 * TODO need snapshot isolation to see consistent
		 * item and bna...
		 */
		AuctionItem item = em.find(AuctionItem.class, itemId);
		if (item != null && AuctionItem.isSold(item)) {
			bna = em.find(BuyNowAuction.class,
					itemId);
			if (bna != null) {
				int buyerId = bna.getBuyerId();
				buyer = em.find(User.class, buyerId);
			}
		}

		return resultOf(item, seller, bna, buyer, param, res);
	}

}
