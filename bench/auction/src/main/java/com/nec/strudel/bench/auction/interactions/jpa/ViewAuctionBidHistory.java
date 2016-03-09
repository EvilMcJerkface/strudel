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

import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewAuctionBidHistory;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewAuctionBidHistory
        extends AbstractViewAuctionBidHistory<EntityManager>
        implements Interaction<EntityManager> {
    public static final String JOIN_QUERY =
            "SELECT b,u FROM Bid b JOIN b.user u WHERE b.sellerId = :sid AND b.itemNo = :ino";
    public static final String GET_QUERY =
            "SELECT b FROM Bid b WHERE b.sellerId = :sid AND b.itemNo = :ino";
    public static final String FETCH_JOIN_QUERY =
            "SELECT b FROM Bid b JOIN FETCH b.user WHERE b.sellerId = :sid AND b.itemNo = :ino";
    public static final String P_SELLER = "sid";
    public static final String P_ITEM_NO = "ino";

    @Override
    public Result execute(Param param, EntityManager em, ResultBuilder res) {
        ItemId itemId = getItemId(param);
        return executeWithJoin(itemId, em, res);
    }

    public Result executeWithFind(ItemId itemId, EntityManager em,
            ResultBuilder res) {
        List<User> bidders = new ArrayList<User>();
        List<Bid> bids = em.createQuery(GET_QUERY, Bid.class)
                .setParameter(P_SELLER, itemId.getSellerId())
                .setParameter(P_ITEM_NO, itemId.getItemNo())
                .getResultList();

        for (Bid bid : bids) {
            User bidder = em.find(User.class,
                    bid.getUserId());
            if (bidder != null) {
                bidders.add(bidder);
            }
        }
        return resultOf(bids, bidders, res);
    }

    public Result executeWithJoin(ItemId itemId, EntityManager em,
            ResultBuilder res) {
        List<Bid> bids = new ArrayList<Bid>();
        List<User> bidders = new ArrayList<User>();
        for (Object r : em.createQuery(JOIN_QUERY)
                .setParameter(P_SELLER, itemId.getSellerId())
                .setParameter(P_ITEM_NO, itemId.getItemNo())
                .getResultList()) {
            Object[] record = (Object[]) r;
            Bid bid = (Bid) record[0];
            User user = (User) record[1];
            bids.add(bid);
            bidders.add(user);
        }
        return resultOf(bids, bidders, res);

    }

    public Result executeWithFetchJoin(ItemId itemId, EntityManager em,
            ResultBuilder res) {
        List<User> bidders = new ArrayList<User>();
        List<Bid> bids = em.createQuery(FETCH_JOIN_QUERY, Bid.class)
                .setParameter(P_SELLER, itemId.getSellerId())
                .setParameter(P_ITEM_NO, itemId.getItemNo())
                .getResultList();

        for (Bid bid : bids) {
            User bidder = bid.getUser();
            if (bidder != null) {
                bidders.add(bidder);
            }
        }
        return resultOf(bids, bidders, res);

    }
}
