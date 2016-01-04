package com.nec.strudel.bench.auction.populate.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.populate.AuctionParamSet;
import com.nec.strudel.bench.auction.populate.AuctionParamSet.AuctionParam;
import com.nec.strudel.bench.auction.populate.base.AbstractPopulateAuctionItem;
import com.nec.strudel.workload.api.Populator;

public class PopulateAuctionItem extends AbstractPopulateAuctionItem<EntityManager>
implements Populator<EntityManager, AuctionParamSet> {

	@Override
	public void process(EntityManager em, AuctionParamSet aps) {
		int sellerId = aps.sellerId();
		em.getTransaction().begin();
		for (AuctionParam p : aps.getParams()) {
			AuctionItem item = new AuctionItem(sellerId);
			p.build(item);
			em.persist(item);
			/**
			 * NOTE: flush is needed to know the automatically
			 * generated value (itemNo) by the above insertion.
			 * This value is used for bids and BNAs below:
			 */
			em.flush();
			int totalBids = p.numOfBids();
			List<Bid> bids = new ArrayList<Bid>(totalBids);
			for (int i = 1; i <= totalBids; i++) {
				bids.add(new Bid(item.getItemId()));
			}
			p.build(bids);
			for (Bid bid : bids) {
				em.persist(bid);
			}
			BuyNowAuction bna = p.bnaIfExists(item);
			if (bna != null) {
				em.persist(bna);
			}
		}
		em.getTransaction().commit();
	}

	@Override
	protected List<AuctionItem> getItemsBySeller(EntityManager em, int sellerId) {
		return em.createQuery("SELECT a FROM AuctionItem a WHERE a.sellerId = :sid",
				AuctionItem.class)
				.setParameter("sid", sellerId)
				.getResultList();
	}

	@Override
	protected AuctionItem getItem(EntityManager em, ItemId id) {
		return em.find(AuctionItem.class, id);
	}

	@Override
	protected BuyNowAuction getBuyNowAuction(EntityManager em, ItemId id) {
		return em.find(BuyNowAuction.class, id);
	}

	@Override
	protected List<Bid> getBidsByItemId(EntityManager em, ItemId id) {
		return em.createQuery("SELECT b FROM Bid b WHERE b.sellerId= :sid"
				+ " AND b.itemNo = :ino", Bid.class)
				.setParameter("sid", id.getSellerId())
				.setParameter("ino", id.getItemNo())
				.getResultList();
	}


}
