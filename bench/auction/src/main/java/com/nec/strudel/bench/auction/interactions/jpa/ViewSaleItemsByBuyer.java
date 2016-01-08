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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleItemsByBuyer;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewSaleItemsByBuyer extends AbstractViewSaleItemsByBuyer<EntityManager>
implements Interaction<EntityManager> {
	public static final String Q_BNS =
			"SELECT b FROM BuyNowSale b WHERE b.buyerId = :buid";
	public static final String Q_BNS_ITEM =
			"SELECT b, s FROM BuyNowSale b, SaleItem s WHERE b.buyerId = :buid"
			+ " AND b.sellerId = s.sellerId AND b.itemNo = s.itemNo";
	public static final String P_BUYER_ID = "buid";
	@Override
	public Result execute(Param param, EntityManager em, ResultBuilder res) {
		int buyerId = getBuyerId(param);

		List<SaleItem> itemList = new ArrayList<SaleItem>();
		List<BuyNowSale> bnsList = new ArrayList<BuyNowSale>();
		for (Object o : em.createQuery(Q_BNS_ITEM)
				.setParameter(P_BUYER_ID, buyerId)
				.getResultList()) {
			Object[] tuple = (Object[]) o;
			BuyNowSale bns = (BuyNowSale) tuple[0];
			SaleItem item = (SaleItem) tuple[1];
			bnsList.add(bns);
			itemList.add(item);
		}
		return resultOf(itemList, bnsList, res);
	}
	public Result executeByFind(int buyerId, EntityManager em, ResultBuilder res) {

		List<SaleItem> itemList = new ArrayList<SaleItem>();
		List<BuyNowSale> bnsList =
				em.createQuery(Q_BNS, BuyNowSale.class)
				.setParameter(P_BUYER_ID, buyerId)
				.getResultList();
		for (BuyNowSale bns : bnsList) {
			SaleItem item = em.find(SaleItem.class, bns.getItemId());
			if (item != null) {
				itemList.add(item);
			}
		}
		return resultOf(itemList, bnsList, res);
	}

}
