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

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionItemsByBuyer;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewAuctionItemsByBuyer
        extends AbstractViewAuctionItemsByBuyer<EntityManager>
        implements Interaction<EntityManager> {
    public static final String Q_BNA = "SELECT b FROM BuyNowAuction b WHERE b.buyerId = :bid";
    public static final String Q_BNA_ITEM = "SELECT b, a FROM BuyNowAuction b, AuctionItem a"
            + " WHERE b.buyerId = :bid"
            + " AND b.sellerId = a.sellerId AND b.itemNo = a.itemNo";

    public static final String P_BUYER = "bid";

    @Override
    public Result execute(Param param, EntityManager em, ResultBuilder res) {
        int buyerId = getBuyerId(param);

        List<BuyNowAuction> bnaList = new ArrayList<BuyNowAuction>();
        List<AuctionItem> itemList = new ArrayList<AuctionItem>();
        for (Object o : em.createQuery(Q_BNA_ITEM)
                .setParameter(P_BUYER, buyerId)
                .getResultList()) {
            Object[] tuple = (Object[]) o;
            BuyNowAuction bna = (BuyNowAuction) tuple[0];
            AuctionItem item = (AuctionItem) tuple[1];
            bnaList.add(bna);
            itemList.add(item);
        }
        return resultOf(itemList, bnaList, res);
    }

    /**
     * an alternate (naive) way to execute.
     */
    public Result executeByFind(int buyerId, EntityManager em,
            ResultBuilder res) {
        List<BuyNowAuction> bnaList = em.createQuery(Q_BNA, BuyNowAuction.class)
                .setParameter(P_BUYER, buyerId)
                .getResultList();
        List<AuctionItem> itemList = new ArrayList<AuctionItem>();
        for (BuyNowAuction bna : bnaList) {
            AuctionItem item = em.find(AuctionItem.class, bna.getItemId());
            if (item != null) {
                itemList.add(item);
            } else {
                res.warn("auction item (" + bna.getItemId()
                        + ") not found for buyer="
                        + buyerId);
            }
        }
        return resultOf(itemList, bnaList, res);

    }

}
