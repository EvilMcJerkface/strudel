package com.nec.strudel.bench.auction.interactions.jpa;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreBid;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class StoreBid extends AbstractStoreBid<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {

		Bid bid = createBid(param);

		em.getTransaction().begin();
		Result r = null;
		try {
			r = store(bid, em, res);
			return r;
		} finally {
			if (r != null && r.isSuccess()) {
				em.getTransaction().commit();
			} else {
				em.getTransaction().rollback();
			}
		}
	}

	Result store(Bid bid, EntityManager em, ResultBuilder res) {
		AuctionItem item =
                em.find(AuctionItem.class, bid.getAuctionItemId(),
                		LockModeType.PESSIMISTIC_WRITE);
		Result r = check(bid, item, res);
		if (r.isSuccess()) {
			em.persist(bid);
	        item.setMaxBid(bid.getBidAmount());
		}
		return r;
	}
}
