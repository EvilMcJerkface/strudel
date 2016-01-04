package com.nec.strudel.bench.auction.interactions.entity;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractSellAuctionItem;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

/**
 * Creates a new auction item that is sold by the current
 * user.
 *
 */
public class SellAuctionItem extends AbstractSellAuctionItem<EntityDB>
implements Interaction<EntityDB> {
	@Override
    public Result execute(Param param, EntityDB db, ResultBuilder res) {
        AuctionItem item = createItem(param);

        db.create(item);

        return res.success();
    }

}
