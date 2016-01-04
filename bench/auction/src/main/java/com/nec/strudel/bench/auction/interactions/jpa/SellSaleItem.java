package com.nec.strudel.bench.auction.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractSellSaleItem;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class SellSaleItem extends AbstractSellSaleItem<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager db, ResultBuilder res) {
		SaleItem item = createItem(param);

		db.getTransaction().begin();
		db.persist(item);
		db.getTransaction().commit();
		return res.success();
	}

}
