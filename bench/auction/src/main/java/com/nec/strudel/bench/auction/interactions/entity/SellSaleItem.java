package com.nec.strudel.bench.auction.interactions.entity;

import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractSellSaleItem;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

/**
 * Creates a new sale item that is sold by the
 * current user.
 *
 */
public class SellSaleItem extends AbstractSellSaleItem<EntityDB>
implements Interaction<EntityDB> {
	@Override
    public Result execute(Param param, EntityDB db, ResultBuilder res) {

        SaleItem item = createItem(param);

        db.create(item);

        return res.success();
    }
}
