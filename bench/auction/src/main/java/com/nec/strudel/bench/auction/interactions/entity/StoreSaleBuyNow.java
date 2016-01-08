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

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreSaleBuyNow;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

/**
 * Tries to buy a sale item, which is specified by SALE_ITEM_ID.
 * It fails it the current
 * available quantity is smaller than the quantity to buy (SALE_NO_QTY).
 *
 */
public class StoreSaleBuyNow extends AbstractStoreSaleBuyNow<EntityDB>
implements Interaction<EntityDB> {
	@Override
	public Result execute(Param param, EntityDB db, ResultBuilder res) {

		BuyNowSale bns = createBuyNowSale(param);

		return db.run(bns, update(bns, res));
	}

	public EntityTask<Result> update(
			final BuyNowSale bns, final ResultBuilder res) {
        return new EntityTask<Result>() {
            @Override
            public Result run(EntityTransaction tx) {
				SaleItem sItem =
                     tx.get(SaleItem.class, bns.getItemId());
				Result r = check(bns, sItem, res);
				if (r.isSuccess()) {
					int newQnty = sItem.getQnty()
	    					- bns.getQnty();
					tx.create(bns);
	            	sItem.setQnty(newQnty);
	    			tx.update(sItem);
				}
				return r;
            }
        };
    }


}
