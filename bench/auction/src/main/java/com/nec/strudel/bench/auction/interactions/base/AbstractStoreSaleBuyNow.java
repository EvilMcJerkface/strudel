package com.nec.strudel.bench.auction.interactions.base;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.SaleItem;
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

public abstract class AbstractStoreSaleBuyNow<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		QNTY_TO_BUY,
		BNS_DATE,
	}

	@Override
	public void prepare(ParamBuilder builder) {
	
		int availableQuantity = builder.getInt(TransParam.QNTY);
		int qntyAdjuster = builder.getInt(SessionParam.QNTY_ADJUSTER);
		int qntyToBuy = builder.getRandomInt(1,
				Math.max(1, availableQuantity / qntyAdjuster));
	
		builder
		.use(SessionParam.USER_ID)
		.use(TransParam.SALE_ITEM_ID)
	    .set(InParam.QNTY_TO_BUY, qntyToBuy)
	    .set(InParam.BNS_DATE, ParamUtil.now());
	}

	@Override
	public void complete(StateModifier modifier) {
	    // do nothing
	}

	public BuyNowSale createBuyNowSale(Param param) {
		ItemId itemId =
				param.getObject(TransParam.SALE_ITEM_ID);
	
	
		BuyNowSale bns = new BuyNowSale(itemId);
		bns.setBuyerId(param.getInt(SessionParam.USER_ID));
		bns.setBnsDate(param.getLong(InParam.BNS_DATE));
		bns.setQnty(param.getInt(InParam.QNTY_TO_BUY));
		return bns;
	}

	public Result check(BuyNowSale bns, SaleItem sItem, ResultBuilder res) {
		res.begin();
		if (sItem == null) {
			res.warn(
			"bns failure: item missing: bns="
				+ bns.getId());
			return res.failure(ResultMode.SALE_NO_QTY);
		}
		// store bns and update item quantity only if
		// there are enough items left
		if (sItem.getQnty() < bns.getQnty()) {
			res.warn(
			"bns failure: item sold"
			+ " (resulted in dangling bns index): bns="
				+ bns.getId());
			return res.failure(ResultMode.SALE_NO_QTY);
		}
		return res.success();
	}

}