package com.nec.strudel.bench.auction.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractSellAuctionItem;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class SellAuctionItem extends AbstractSellAuctionItem<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager db, ResultBuilder res) {

		AuctionItem item = createItem(param);
		db.getTransaction().begin();
		db.persist(item);
		db.getTransaction().commit();

		return res.success();
	}

}
