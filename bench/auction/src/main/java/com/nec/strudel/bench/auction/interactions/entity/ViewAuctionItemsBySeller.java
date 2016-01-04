package com.nec.strudel.bench.auction.interactions.entity;

import java.util.List;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionItemsBySeller;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewAuctionItemsBySeller extends AbstractViewAuctionItemsBySeller<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		int sellerId = getSellerId(param);

		List<AuctionItem> itemList =
				db.getEntitiesByIndex(AuctionItem.class,
						"sellerId", sellerId);
		return resultOf(itemList, param, res);
	}
}
