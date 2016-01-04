package com.nec.strudel.bench.auction.interactions.jpa;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreSaleBuyNow;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class StoreSaleBuyNow extends AbstractStoreSaleBuyNow<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {

		BuyNowSale bns = createBuyNowSale(param);
		em.getTransaction().begin();
		Result r = null;
		try {
			r = store(bns, em, res);
			return r;
		} finally {
			if (r != null && r.isSuccess()) {
				em.getTransaction().commit();
			} else {
				em.getTransaction().rollback();
			}
		}
	}

	Result store(BuyNowSale bns, EntityManager em, ResultBuilder res) {
		SaleItem sItem =
				em.find(SaleItem.class, bns.getItemId(),
						LockModeType.PESSIMISTIC_WRITE);
		Result r = check(bns, sItem, res);
		if (r.isSuccess()) {
			int newQnty = sItem.getQnty()
					- bns.getQnty();
			em.persist(bns);
			sItem.setQnty(newQnty);
		}
		return r;
	}
}
