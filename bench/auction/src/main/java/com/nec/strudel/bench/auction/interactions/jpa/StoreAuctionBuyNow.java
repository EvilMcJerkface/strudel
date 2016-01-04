package com.nec.strudel.bench.auction.interactions.jpa;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.apache.log4j.Logger;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreAuctionBuyNow;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class StoreAuctionBuyNow extends AbstractStoreAuctionBuyNow<EntityManager>
implements Interaction<EntityManager> {
	private static final Logger LOGGER = Logger.getLogger(StoreAuctionBuyNow.class);
	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {

		BuyNowAuction bna = createBna(param);
		em.getTransaction().begin();
		Result r = null;
		try {
			r = store(bna, em, res);
			return r;
		} finally {
			if (em.getTransaction().isActive()) {
				if (r != null && r.isSuccess()) {
					em.getTransaction().commit();
				} else {
					em.getTransaction().rollback();
				}
			}
		}
	}
	private Result store(BuyNowAuction bna, EntityManager em,  ResultBuilder res) {
        AuctionItem item =
        		em.find(AuctionItem.class, bna.getItemId(),
        				LockModeType.PESSIMISTIC_WRITE);
        Result r = check(bna, item, res);
        if (r.isSuccess()) {
        	AuctionItem.sell(item);
            try {
				em.persist(bna);
				em.getTransaction().commit();
			} catch (EntityExistsException e) {
                return res.warn("auction is not sold but BNA exists: "
                        + bna.getItemId())
                   .failure(ResultMode.UNKNOWN_ERROR);
			} catch (Exception e) {
				LOGGER.error(
						"unexpected error when inserting BNA", e);

                return res.warn("unexpected exception: " + e.getMessage())
                   .failure(ResultMode.UNKNOWN_ERROR);
				
			}
        }
        return r;
	}

}
