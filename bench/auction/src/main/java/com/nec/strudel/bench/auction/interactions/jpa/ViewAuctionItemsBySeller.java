package com.nec.strudel.bench.auction.interactions.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionItemsBySeller;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewAuctionItemsBySeller extends AbstractViewAuctionItemsBySeller<EntityManager>
implements Interaction<EntityManager> {
	public static final String QUERY = "SELECT a FROM AuctionItem a WHERE a.sellerId = :sid";
	public static final String PARAM = "sid";
	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int sellerId = getSellerId(param);

		List<AuctionItem> itemList =
				em.createQuery(QUERY, AuctionItem.class)
				.setParameter(PARAM, sellerId)
				.getResultList();
		return resultOf(itemList, param, res);
	}
}
