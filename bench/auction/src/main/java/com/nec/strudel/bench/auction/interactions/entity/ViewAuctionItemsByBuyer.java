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
package com.nec.strudel.bench.auction.interactions.entity;

import java.util.ArrayList;
import java.util.List;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionItemsByBuyer;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewAuctionItemsByBuyer extends AbstractViewAuctionItemsByBuyer<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {
		int buyerId = getBuyerId(param);

		List<AuctionItem> itemList =
				new ArrayList<AuctionItem>();
		List<BuyNowAuction> bnaList = 
				db.getEntitiesByIndex(BuyNowAuction.class, "buyerId", buyerId);
		for (BuyNowAuction bna : bnaList) {
			AuctionItem item = db.get(AuctionItem.class, bna.getItemId());
			if (item != null) {
				itemList.add(item);
			} else {
				res.warn("bna (" + bna.getItemId()
						+ ") not found for buyer="
						+ buyerId);
			}
		}
		return resultOf(itemList, bnaList, res);
	}

	/**
	 * an alternative way to execute each pair
	 * of ItemId and BuyNowAuction (that are in the
	 * same group) in the same transaction.
	 */
	public void executeWithTransactions(int buyerId,
			EntityDB db, List<AuctionItem> itemList,
			List<BuyNowAuction> bnaList,  ResultBuilder res) {
		for (ItemId id : db.scanIds(ItemId.class,
				BuyNowAuction.class, "buyerId", buyerId)) {
			EntityPair<AuctionItem, BuyNowAuction> pair =
					db.run(AuctionItem.class, id,
							getAuctionItemBNA(id));

			AuctionItem auctionItem = pair.getFirst();
			BuyNowAuction bna = pair.getSecond();

			if (auctionItem != null) {
				itemList.add(auctionItem);
			} else {
				res.warn("auction item (" + id
						+ ") not found for buyer="
						+ buyerId);
			}
			if (bna != null) {
				bnaList.add(bna);
			} else {
				res.warn("bna (" + id
						+ ") not found for buyer="
						+ buyerId);
			}
		}
		
	}

	public EntityTask<EntityPair<AuctionItem, BuyNowAuction>>
	getAuctionItemBNA(final ItemId itemKey) {
		return new EntityTask<EntityPair<AuctionItem,
							BuyNowAuction>>() {
			@Override
			public EntityPair<AuctionItem, BuyNowAuction> run(
					EntityTransaction tx) {
				AuctionItem auctionItem =
					tx.get(AuctionItem.class, itemKey);
				BuyNowAuction bna =
					tx.get(BuyNowAuction.class, itemKey);
				return EntityPair.of(auctionItem, bna);
			}
		};
	}
}
