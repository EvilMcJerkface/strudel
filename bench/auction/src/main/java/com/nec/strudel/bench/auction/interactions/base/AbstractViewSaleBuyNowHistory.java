/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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