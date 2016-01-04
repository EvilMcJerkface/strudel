package com.nec.strudel.bench.auction.interactions.entity;

import java.util.ArrayList;
import java.util.List;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.SaleId;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleItemsByBuyer;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewSaleItemsByBuyer extends AbstractViewSaleItemsByBuyer<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {

		int buyerId = getBuyerId(param);

		List<SaleItem> itemList = new ArrayList<SaleItem>();
		List<BuyNowSale> bnsList =
				db.getEntitiesByIndex(BuyNowSale.class, "buyerId", buyerId);

		for (BuyNowSale bns : bnsList) {
			SaleItem saleItem = db.get(SaleItem.class, bns.getItemId());
			if (saleItem != null) {
				itemList.add(saleItem);
			} else {
				res.warn("sale item not found for id =" + bns.getItemId());
			}
		}
		return resultOf(itemList, bnsList, res);
	}

	public void executeWithTransactions(int buyerId, EntityDB db,
			List<SaleItem> itemList, List<BuyNowSale> bnsList, ResultBuilder res) {
		for (SaleId id : db.scanIds(SaleId.class, BuyNowSale.class,
				"buyerId", buyerId)) {
			EntityPair<SaleItem, BuyNowSale> pair =
					db.run(BuyNowSale.class, id,
						getSaleItemBNS(id));

			SaleItem saleItem = pair.getFirst();
			BuyNowSale bns = pair.getSecond();
			if (saleItem != null) {
				itemList.add(saleItem);
			} else {
				res.warn("sale item not found for id =" + id);
			}
			if (bns != null) {
				bnsList.add(bns);
			}
		}
		
	}

	public EntityTask<EntityPair<SaleItem, BuyNowSale>> getSaleItemBNS(
			final SaleId bnsKey) {
        return new EntityTask<EntityPair<SaleItem, BuyNowSale>>() {
            @Override
            public EntityPair<SaleItem, BuyNowSale> run(EntityTransaction tx) {
            	BuyNowSale bns = null;
            	SaleItem saleItem =
                    tx.get(SaleItem.class, bnsKey.getItemId());
            	if (saleItem != null) {
            		bns = tx.get(BuyNowSale.class, bnsKey);
            	}
				return EntityPair.of(saleItem, bns);
            }
        };
    }

}
