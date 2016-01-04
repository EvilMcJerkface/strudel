package com.nec.strudel.bench.auction.interactions.base;

import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractViewSaleItem<T> implements Interaction<T> {

	public enum OutParam implements LocalParam {
		SELLER,
		SALE_ITEM
	}

	@Override
	public void prepare(ParamBuilder builder) {
		builder.use(TransParam.SALE_ITEM_ID);
	}

	@Override
	public void complete(StateModifier modifier) {
		modifier
		.export(TransParam.SALE_ITEM_ID)
		.export(TransParam.QNTY);
	}

	public ItemId getItemId(Param param) {
		return param.getObject(
				TransParam.SALE_ITEM_ID);
	}

	public Result resultOf(SaleItem saleItem, User seller, Param param, ResultBuilder res) {
		if (saleItem == null) {
			ItemId saleItemId = getItemId(param);
			return res.warn("sale item not found: id="
						+ saleItemId)
				.success(ResultMode.EMPTY_RESULT);
		}
		if (seller == null) {
			int sellerId = saleItem.getSellerId();
			res.warn("user not found: id=" + sellerId);
		}
		res
		.set(OutParam.SELLER, seller)
		.set(OutParam.SALE_ITEM, saleItem)
		.set(TransParam.SALE_ITEM_ID, saleItem.getId())
		.set(TransParam.QNTY, saleItem.getQnty());
		if (saleItem.getQnty() > 0) {
			return res.success();
		} else {
			return res.success(ResultMode.SALE_NO_QTY);
		}
	}

}