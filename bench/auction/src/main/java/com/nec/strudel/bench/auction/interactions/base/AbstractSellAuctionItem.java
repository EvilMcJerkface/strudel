package com.nec.strudel.bench.auction.interactions.base;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractSellAuctionItem<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		NAME,
		INITIAL_BID,
		BUYNOW,
		MAX_BID,
		AUCTION_END_DATE
	}

	@Override
	public void prepare(ParamBuilder builder) {
	
	    double initialBid = builder.getRandomDouble(
	    		SessionParam.AUCTION_INIT_BID_MIN,
	    		SessionParam.AUCTION_INIT_BID_MAX);
	    double buyNowRatio = builder.getRandomDouble(
	    		SessionParam.AUCTION_BUY_NOW_RATIO_MIN,
	    		SessionParam.AUCTION_BUY_NOW_RATIO_MAX);
	    int daysToEnd = builder.getRandomInt(
	    		SessionParam.AUCTION_DURATION_DATE_MIN,
	    		SessionParam.AUCTION_DURATION_DATE_MAX);
	
		builder
		.use(SessionParam.USER_ID)
		.randomAlphaString(InParam.NAME, SessionParam.ITEM_NAME_LEN)
		.set(InParam.INITIAL_BID, initialBid)
	    .set(InParam.BUYNOW, initialBid * buyNowRatio)
	    .set(InParam.MAX_BID, initialBid)
	    .set(InParam.AUCTION_END_DATE, ParamUtil.dayAfter(daysToEnd));
	}

	@Override
	public void complete(StateModifier modifier) {
	    // do nothing
	}

	public AuctionItem createItem(Param param) {
	    int sellerId = param.getInt(SessionParam.USER_ID);
	
	    AuctionItem item = new AuctionItem(sellerId);
	    item.setItemName(param.get(InParam.NAME));
	    item.setInitialBid(param.getDouble(InParam.INITIAL_BID));
	    item.setBuyNow(param.getDouble(InParam.BUYNOW));
	    item.setMaxBid(param.getDouble(InParam.MAX_BID));
	    item.setEndDate(param.getLong(InParam.AUCTION_END_DATE));
		return item;
	}

}