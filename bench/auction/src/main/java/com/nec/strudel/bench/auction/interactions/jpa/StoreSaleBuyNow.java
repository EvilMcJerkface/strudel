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
import javax.persistence.LockModeType;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreSaleBuyNow;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class StoreSaleBuyNow extends AbstractStoreSaleBuyNow<EntityManager>
implements Interaction<EntityManager> {

	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {

		BuyNowSale bns = createBuyNowSale(param);
		em.getTransaction().begin();
		Result r = null;
		try {
			r = store(bns, em, res);
			return r;
		} finally {
			if (r != null && r.isSuccess()) {
				em.getTransaction().commit();
			} else {
				em.getTransaction().rollback();
			}
		}
	}

	Result store(BuyNowSale bns, EntityManager em, ResultBuilder res) {
		SaleItem sItem =
				em.find(SaleItem.class, bns.getItemId(),
						LockModeType.PESSIMISTIC_WRITE);
		Result r = check(bns, sItem, res);
		if (r.isSuccess()) {
			int newQnty = sItem.getQnty()
					- bns.getQnty();
			em.persist(bns);
			sItem.setQnty(newQnty);
		}
		return r;
	}
}
