package com.nec.strudel.bench.auction.interactions.entity;

import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleItem;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewSaleItem extends AbstractViewSaleItem<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {

		ItemId saleItemId = getItemId(param);

		SaleItem saleItem = db.get(SaleItem.class, saleItemId);
		User seller = db.get(User.class, saleItemId.getSellerId());

		return resultOf(saleItem, seller, param, res);
	}
}
