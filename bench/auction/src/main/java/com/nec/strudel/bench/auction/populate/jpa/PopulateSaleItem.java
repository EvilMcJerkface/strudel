package com.nec.strudel.bench.auction.populate.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.populate.SaleParamSet;
import com.nec.strudel.bench.auction.populate.SaleParamSet.SaleParam;
import com.nec.strudel.bench.auction.populate.base.AbstractPopulateSaleItem;
import com.nec.strudel.workload.api.Populator;

public class PopulateSaleItem extends AbstractPopulateSaleItem<EntityManager>
implements Populator<EntityManager, SaleParamSet> {

	@Override
	public void process(EntityManager em, SaleParamSet sps) {
		int sellerId = sps.sellerId();
		em.getTransaction().begin();
		for (SaleParam p : sps.getParams()) {
			SaleItem item = new SaleItem(sellerId);
			p.build(item);
			em.persist(item);
			/**
			 * flush is done in order to get the automatically
			 * generated value (itemNo) by the above insertion.
			 * The value is used for BNSs below:
			 */
			em.flush();
			int buyNowNum = p.numOfBns();
			List<BuyNowSale> bnsList =
					new ArrayList<BuyNowSale>(buyNowNum);
			for (int i = 1; i <= buyNowNum; i++) {
				bnsList.add(new BuyNowSale(item.getId()));
			}
			p.build(bnsList);
			for (BuyNowSale bns : bnsList) {
				em.persist(bns);
			}
		}
		em.getTransaction().commit();
	}


}
