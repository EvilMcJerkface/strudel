package com.nec.strudel.bench.auction.interactions.base;

import java.util.List;

import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

/**
 * Retrieves sale items sold by a user, which is
 * chosen randomly from the user set (except the current user).
 * The successful result may have the following mode:
 * <ul>
 * <li> EMPTY_RESULT: there is not sale item sold
 * by the chosen user.
 * </ul>
 * It modifies the transition state:
 * <ul>
 * <li> SALE_ITEM_ID: The ID of randomly chosen sale item
 * from the result set is set if the result set is not empty.
 * </ul>
 *
 */
public abstract class AbstractViewSaleItemsBySeller<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		SELLER_ID
	}

	public enum OutParam implements LocalParam {
		SALE_ITEM_LIST,
	}

	@Override
	public void prepare(ParamBuilder builder) {
		builder.randomIntId(InParam.SELLER_ID,
				SessionParam.MIN_USER_ID,
				SessionParam.TOTAL_USER,
				SessionParam.USER_ID);
	}

	@Override
	public void complete(StateModifier modifier) {
		SaleItem item = modifier.getOne(
				OutParam.SALE_ITEM_LIST);
		if (item != null) {
			modifier.set(TransParam.SALE_ITEM_ID,
					item.getId());
		}
	}

	public int getSellerId(Param param) {
		return param.getInt(InParam.SELLER_ID);
	}

	public Result resultOf(List<SaleItem> itemList, Param param, ResultBuilder res) {
		res.set(OutParam.SALE_ITEM_LIST, itemList);
	
		if (itemList.isEmpty()) {
			int sellerId = getSellerId(param);
			res.warn("no sale item for seller = " + sellerId);
			return res.success(ResultMode.EMPTY_RESULT);
		} else {
			return res.success();
		}
	}

}