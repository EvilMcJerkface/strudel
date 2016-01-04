package com.nec.strudel.bench.auction.interactions.base;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractViewSaleBuyNowHistory<T> implements Interaction<T> {

	public enum OutParam implements LocalParam {
		BUYER_LIST,
		BNS_LIST,
	}

	@Override
	public void prepare(ParamBuilder builder) {
		builder.use(TransParam.SALE_ITEM_ID);
	}

	@Override
	public void complete(StateModifier modifier) {
	    // do nothing
	}

	public ItemId getItemId(Param param) {
		return param.getObject(TransParam.SALE_ITEM_ID);
	}

	public Result resultOf(List<BuyNowSale> bnsList, List<User> buyers, ResultBuilder res) {
		Set<Integer> uids = new HashSet<Integer>();
		for (User u : buyers) {
			uids.add(u.getUserId());
		}
		for (BuyNowSale bns : bnsList) {
			if (!uids.contains(bns.getBuyerId())) {
				res.warn("the buyer of " + bns.getId()
						+ " not found: uid="
						+ bns.getBuyerId());
			}
		}
		return res
				.set(OutParam.BNS_LIST, bnsList)
				.set(OutParam.BUYER_LIST, buyers)
				.success();
		
	}

}