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

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreBid;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class StoreBid extends AbstractStoreBid<EntityManager>
        implements Interaction<EntityManager> {

    @Override
    public Result execute(Param param, EntityManager em, ResultBuilder res) {

        Bid bid = createBid(param);

        em.getTransaction().begin();
        Result execResult = null;
        try {
            execResult = store(bid, em, res);
            return execResult;
        } finally {
            if (execResult != null && execResult.isSuccess()) {
                em.getTransaction().commit();
            } else {
                em.getTransaction().rollback();
            }
        }
    }

    Result store(Bid bid, EntityManager em, ResultBuilder res) {
        AuctionItem item = em.find(AuctionItem.class, bid.getAuctionItemId(),
                LockModeType.PESSIMISTIC_WRITE);
        Result execResult = check(bid, item, res);
        if (execResult.isSuccess()) {
            em.persist(bid);
            item.setMaxBid(bid.getBidAmount());
        }
        return execResult;
    }
}
