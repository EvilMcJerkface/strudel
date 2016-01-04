package com.nec.strudel.bench.auction.interactions.entity;

import java.util.List;

import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleItemsBySeller;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewSaleItemsBySeller extends AbstractViewSaleItemsBySeller<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {

		int sellerId = getSellerId(param);

		List<SaleItem> itemList =
				db.getEntitiesByIndex(SaleItem.class,
						"sellerId", sellerId);

		return resultOf(itemList, param, res);
	}
}
