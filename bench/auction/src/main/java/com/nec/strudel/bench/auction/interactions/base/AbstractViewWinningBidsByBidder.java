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
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractViewWinningBidsByBidder<T> implements Interaction<T> {

	public enum OutParam implements LocalParam {
		WIN_ITEM_LIST,
		WIN_BIDS
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

	public int getBidderId(Param param) {
		return param.getInt(SessionParam.USER_ID);
	}

	public Result resultOf(List<AuctionItem> winItemList, List<Bid> winBids, ResultBuilder res) {
		res.set(OutParam.WIN_ITEM_LIST, winItemList)
		.set(OutParam.WIN_BIDS, winBids);
	
	return res.success();
	}

	public boolean isWinning(Bid bid, AuctionItem item) {
		if (AuctionItem.isSold(item)) {
			return false;
		}
		if (item.getEndDate() > ParamUtil.now()) {
			return false; // still open
		}
		return bid.getBidAmount() == item.getMaxBid();
	}

}