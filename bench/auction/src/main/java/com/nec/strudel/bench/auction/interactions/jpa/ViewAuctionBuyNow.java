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
package com.nec.strudel.bench.auction.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionBuyNow;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewAuctionBuyNow extends AbstractViewAuctionBuyNow<EntityManager>
implements Interaction<EntityManager> {
	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		ItemId itemId = getItemId(param);
		if (itemId == null) {
			return res.warn("AUCTION_ITEM_ID not specified"
					+ " in the parameter: " + param)
					.failure(ResultMode.MISSING_PARAM);
		}

		BuyNowAuction bna = em.find(BuyNowAuction.class, itemId);
		User buyer = null;
		if (bna != null) {
			buyer = em.find(User.class, bna.getBuyerId());
		}
		return resultOf(bna, buyer, param, res);
	}

}
