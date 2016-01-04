package com.nec.strudel.bench.auction.interactions.base;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractStoreBid<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		BID_DATE,
		BID_AMOUNT
	}

	@Override
	public void prepare(ParamBuilder builder) {
	
		double min = builder.getDouble(TransParam.MAX_BID);
		double buyNow =	builder.getDouble(TransParam.BUYNOW);
		int bidAmountAdjuster = builder.getInt(
				SessionParam.BID_AMOUNT_ADJUSTER);
		double max = min + ((buyNow - min) / bidAmountAdjuster);
		double bidAmount = builder.getRandomDouble(min, max);
	
	
		builder
		.use(SessionParam.USER_ID)
		.use(TransParam.AUCTION_ITEM_ID)
		.set(InParam.BID_DATE, ParamUtil.now())
		.set(InParam.BID_AMOUNT, bidAmount);
	}

	@Override
	public void complete(StateModifier modifier) {
	    // do nothing
	}

	public Result check(Bid bid, AuctionItem item, ResultBuilder res) {
		res.begin();
		if (item == null) {
			return res.warn("item not found: " + bid.getAuctionItemId())
					.failure(ResultMode.UNKNOWN_ERROR);
		}
		if (AuctionItem.isSold(item)) {
			res.warn(
					"bid failure: item sold"
							+ " (resulted in dangling bid index): bid="
							+ bid.getId());
			return res.failure(ResultMode.AUCTION_SOLD);
		}
		if (bid.getBidDate() > item.getEndDate()) {
			res.warn(
					"bid failure: item expired"
							+ " (resulted in dangling bid index): bid="
							+ bid.getId());
			return res.failure(ResultMode.AUCTION_EXPIRED);
		}
		if (bid.getBidAmount() <= item.getMaxBid()) {
			res.warn(
					"bid failure: bid price lower than the max"
							+ " (resulted in dangling bid index): bid="
							+ bid.getId());
			return res.failure(ResultMode.LOSING_BID);
		}
		return res.success();
	}

	public Bid createBid(Param param) {
		ItemId itemId = param.getObject(
				TransParam.AUCTION_ITEM_ID);
	
		Bid bid = new Bid(itemId);
		bid.setBidAmount(param.getDouble(InParam.BID_AMOUNT));
		bid.setBidDate(param.getLong(InParam.BID_DATE));
		bid.setUserId(param.getInt(SessionParam.USER_ID));
		return bid;
	}

}