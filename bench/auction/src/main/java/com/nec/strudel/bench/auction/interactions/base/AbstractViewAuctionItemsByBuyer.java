package com.nec.strudel.bench.auction.interactions.base;

import java.util.List;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

/**
 * Retrieves the auction items sold by the current user.
 *
 */
public abstract class AbstractViewAuctionItemsByBuyer<T> implements Interaction<T> {

	public enum OutParam implements LocalParam {
		AUCTION_ITEM_LIST,
		BNA_LIST,
	}

	@Override
	public void prepare(ParamBuilder builder) {
		builder
		.use(SessionParam.USER_ID);
	}

	@Override
	public void complete(StateModifier modifier) {
	    // do nothing
	}

	public int getBuyerId(Param param) {
		return param.getInt(SessionParam.USER_ID);
	}

	public Result resultOf(List<AuctionItem> itemList, List<BuyNowAuction> bnaList, ResultBuilder res) {
		return res
				.set(OutParam.AUCTION_ITEM_LIST, itemList)
				.set(OutParam.BNA_LIST, bnaList)
				.success();
	}

}