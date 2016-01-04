package com.nec.strudel.bench.auction.interactions.entity;

import java.util.ArrayList;
import java.util.List;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleBuyNowHistory;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewSaleBuyNowHistory extends AbstractViewSaleBuyNowHistory<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		ItemId itemId = getItemId(param);

		List<BuyNowSale> bnsList = db.getEntitiesByIndex(
				BuyNowSale.class,
				"itemId", itemId);

		List<User> buyers = new ArrayList<User>();

		for (BuyNowSale bns : bnsList) {
			User buyer = db.get(User.class, bns.getBuyerId());
			if (buyer != null) {
				buyers.add(buyer);
			}
		}

		return resultOf(bnsList, buyers, res);
	}
}
