package com.nec.strudel.bench.auction.interactions.base;

import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractSellSaleItem<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		NAME,
		PRICE,
		QNTY,
	}

	@Override
	public void prepare(ParamBuilder builder) {
		builder
		.use(SessionParam.USER_ID)
		.randomAlphaString(InParam.NAME,
				SessionParam.ITEM_NAME_LEN)
	    .randomDouble(InParam.PRICE, SessionParam.SALE_PRICE_MIN,
	    		SessionParam.SALE_PRICE_MAX)
	    .randomInt(InParam.QNTY, SessionParam.SALE_QNTY_MIN,
	    		SessionParam.SALE_QNTY_MAX);
	}

	@Override
	public void complete(StateModifier modifier) {
	    // do nothing
	}

	public SaleItem createItem(Param param) {
	    int sellerId = param.getInt(SessionParam.USER_ID);
	
	    SaleItem item = new SaleItem(sellerId);
	    item.setItemName(param.get(InParam.NAME));
	    item.setPrice(param.getDouble(InParam.PRICE));
	    item.setQnty(param.getInt(InParam.QNTY));
	    return item;
	}

}