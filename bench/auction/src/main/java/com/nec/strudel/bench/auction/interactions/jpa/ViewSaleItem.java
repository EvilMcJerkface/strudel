package com.nec.strudel.bench.auction.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleItem;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewSaleItem extends AbstractViewSaleItem<EntityManager>
implements Interaction<EntityManager> {
	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {

		ItemId saleItemId = getItemId(param);

		SaleItem saleItem = em.find(SaleItem.class, saleItemId);
		User seller = em.find(User.class, saleItemId.getSellerId());

		return resultOf(saleItem, seller, param, res);
	}
}

