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
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleBuyNowHistory;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewSaleBuyNowHistory
        extends AbstractViewSaleBuyNowHistory<EntityManager>
        implements Interaction<EntityManager> {
    public static final String QUERY =
            "SELECT b FROM BuyNowSale b WHERE b.sellerId = :sid AND b.itemNo = :ino";
    public static final String JOIN_QUERY =
            "SELECT b,u FROM BuyNowSale b, User u WHERE b.sellerId = :sid"
            + " AND b.itemNo = :ino AND b.buyerId = u.userId";
    public static final String P_SELLER = "sid";
    public static final String P_ITEM_NO = "ino";

    @Override
    public Result execute(Param param, EntityManager em, ResultBuilder res) {
        ItemId itemId = getItemId(param);
        List<User> buyers = new ArrayList<User>();

        List<BuyNowSale> bnsList = new ArrayList<BuyNowSale>();
        for (Object o : em.createQuery(JOIN_QUERY)
                .setParameter(P_SELLER, itemId.getSellerId())
                .setParameter(P_ITEM_NO, itemId.getItemNo())
                .getResultList()) {
            Object[] tuple = (Object[]) o;
            BuyNowSale bns = (BuyNowSale) tuple[0];
            User buyer = (User) tuple[1];
            bnsList.add(bns);
            buyers.add(buyer);
        }

        return resultOf(bnsList, buyers, res);
    }

    public Result executeByFind(ItemId itemId, EntityManager em,
            ResultBuilder res) {
        List<BuyNowSale> bnsList = em.createQuery(QUERY, BuyNowSale.class)
                .setParameter(P_SELLER, itemId.getSellerId())
                .setParameter(P_ITEM_NO, itemId.getItemNo())
                .getResultList();

        List<User> buyers = new ArrayList<User>();

        for (BuyNowSale bns : bnsList) {
            User buyer = em.find(User.class, bns.getBuyerId());
            if (buyer != null) {
                buyers.add(buyer);
            }
        }

        return resultOf(bnsList, buyers, res);
    }
}
