package com.nec.strudel.bench.auction.interactions.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewBidsByBidder;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewBidsByBidder extends AbstractViewBidsByBidder<EntityManager>
implements Interaction<EntityManager> {
	public static final String Q_BIDS = "SELECT b FROM Bid b WHERE b.userId = :uid";
	public static final String Q_BIDS_ITEM =
			"SELECT b,a FROM Bid b, AuctionItem a WHERE b.userId = :uid"
			+ " AND b.sellerId = a.sellerId AND b.itemNo = a.itemNo";
	public static final String P_BIDDER_ID = "uid";
	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int bidderId = getBidderId(param);

		List<AuctionItem> itemList = new ArrayList<AuctionItem>();
		List<Bid> bidList = new ArrayList<Bid>();
		for (Object o : em.createQuery(Q_BIDS_ITEM)
				.setParameter(P_BIDDER_ID, bidderId)
				.getResultList()) {
			Object[] tuple = (Object[]) o;
			Bid bid = (Bid) tuple[0];
			AuctionItem item = (AuctionItem) tuple[1];
			bidList.add(bid);
			itemList.add(item);
		}
		return resultOf(itemList, bidList, res);
	}

	public Result executeByFind(int bidderId, EntityManager em, ResultBuilder res) {

		List<AuctionItem> itemList = new ArrayList<AuctionItem>();
		List<Bid> bidList =
				em.createQuery(Q_BIDS, Bid.class)
				.setParameter(P_BIDDER_ID, bidderId)
				.getResultList();
		for (Bid b : bidList) {
			AuctionItem item = em.find(AuctionItem.class,
					b.getAuctionItemId());
			if (item != null) {
				itemList.add(item);
			}
		}
		return resultOf(itemList, bidList, res);
	}


}
