package com.nec.strudel.bench.auction.interactions.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleItemsBySeller;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewSaleItemsBySeller extends AbstractViewSaleItemsBySeller<EntityManager>
implements Interaction<EntityManager>  {
	public static final String QUERY =
			"SELECT s FROM SaleItem s WHERE s.sellerId = :sid";
	public static final String PARAM = "sid";
	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int sellerId = getSellerId(param);
		List<SaleItem> itemList =
				em.createQuery(QUERY, SaleItem.class)
				.setParameter(PARAM, sellerId)
				.getResultList();

		return resultOf(itemList, param, res);
	}

}
