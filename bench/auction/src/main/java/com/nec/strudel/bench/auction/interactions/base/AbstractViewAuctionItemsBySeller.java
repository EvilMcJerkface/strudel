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

import java.util.List;

import com.nec.strudel.bench.auction.entity.AuctionItem;
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
 * Retrieves a set of auction items sold by a user
 * (chosen randomly from the set of the users except the
 * current user).
 * The successful result may have the following mode:
 * <ul>
 * <li> EMPTY_RESULT: the specified user has no auction
 * item to sell.
 * </ul>
 * It changes the transient state:
 * <ul>
 * <li> AUCTION_ITEM_ID: the ID of an item randomly
 * chosen from the retrieved set if it is not empty.
 * </ul>
 */
public abstract class AbstractViewAuctionItemsBySeller<T> implements Interaction<T> {

	public enum InParam implements LocalParam {
		SELLER_ID
	}

	public enum OutParam implements LocalParam {
		AUCTION_ITEM_LIST
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
		AuctionItem item = modifier.getOne(
				OutParam.AUCTION_ITEM_LIST);
		if (item != null) {
			modifier.set(TransParam.AUCTION_ITEM_ID,
					item.getItemId());
		}
	}

	public int getSellerId(Param param) {
		return param.getInt(InParam.SELLER_ID);
	}

	public Result resultOf(List<AuctionItem> itemList, Param param, ResultBuilder res) {
		res.set(OutParam.AUCTION_ITEM_LIST, itemList);
	
		if (itemList.isEmpty()) {
			int sellerId = getSellerId(param);
			res.warn("no item found for seller=" + sellerId);
			return res.success(ResultMode.EMPTY_RESULT);
		} else {
			return res.success();
		}
		
	}

}