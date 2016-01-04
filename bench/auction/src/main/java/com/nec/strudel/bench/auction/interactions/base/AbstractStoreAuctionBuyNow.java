package com.nec.strudel.bench.auction.interactions.base;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
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

public abstract class AbstractStoreAuctionBuyNow<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		BNA_DATE
	}


	@Override
	public void prepare(ParamBuilder builder) {
		builder
		.use(SessionParam.USER_ID)
		.use(TransParam.AUCTION_ITEM_ID)
		.set(InParam.BNA_DATE, ParamUtil.now());
	}

	@Override
	public void complete(StateModifier modifier) {
	    // do nothing
	}

	public BuyNowAuction createBna(Param param) {
		ItemId itemId = param.getObject(
				TransParam.AUCTION_ITEM_ID);
		BuyNowAuction bna = new BuyNowAuction(itemId);
		bna.setBnaDate(param.getLong(InParam.BNA_DATE));
		bna.setBuyerId(param.getInt(SessionParam.USER_ID));
		return bna;
	}

	public Result check(BuyNowAuction bna, AuctionItem item, ResultBuilder res) {
		res.begin();
	    if (item == null) {
	    	return res
			.warn("item not found: " + bna.getItemId())
			.failure(ResultMode.UNKNOWN_ERROR);
	    }
	    if (AuctionItem.isSold(item)) {
			return res.failure(ResultMode.AUCTION_SOLD);
	    } else if (bna.getBnaDate() > item.getEndDate()) {
			res.warn(
			"bns failure: item expired"
			+ " (resulted in dangling bna index): bna="
				+ bna.getItemId());
			return res.failure(ResultMode.AUCTION_EXPIRED);
	    } else {
	    	return res.success();
	    }
	}


}