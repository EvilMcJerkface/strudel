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

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionItem;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewAuctionItem extends AbstractViewAuctionItem<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		ItemId itemId = getItemId(param);
		User seller = em.find(User.class, itemId.getSellerId());

		BuyNowAuction bna = null;
		User buyer = null;
		/**
		 * TODO need snapshot isolation to see consistent
		 * item and bna...
		 */
		AuctionItem item = em.find(AuctionItem.class, itemId);
		if (item != null && AuctionItem.isSold(item)) {
			bna = em.find(BuyNowAuction.class,
					itemId);
			if (bna != null) {
				int buyerId = bna.getBuyerId();
				buyer = em.find(User.class, buyerId);
			}
		}

		return resultOf(item, seller, bna, buyer, param, res);
	}

}
