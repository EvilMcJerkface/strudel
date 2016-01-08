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

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreAuctionBuyNow;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

/**
 * Tries to buy the current auction item of interest,
 * which is specified by AUCTION_ITEM_ID.
 * The trial fails if the auction item has been sold
 * (AUCTION_SOLD)
 * or expired (AUCTION_EXPIRED).
 *
 */
public class StoreAuctionBuyNow extends AbstractStoreAuctionBuyNow<EntityDB>
implements Interaction<EntityDB> {

	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {

		BuyNowAuction bna = createBna(param);

		return db.run(bna, update(bna, res));
	}

	protected EntityTask<Result> update(final BuyNowAuction bna, final ResultBuilder res) {
		return new EntityTask<Result>() {
            @Override
            public Result run(EntityTransaction tx) {
                AuctionItem item =
                		tx.get(AuctionItem.class, bna.getItemId());
                Result r = check(bna, item, res);
                if (r.isSuccess()) {
                	tx.create(bna);
                	AuctionItem.sell(item);
                	tx.update(item);
                }
                return r;
            }
		};
	}



}
