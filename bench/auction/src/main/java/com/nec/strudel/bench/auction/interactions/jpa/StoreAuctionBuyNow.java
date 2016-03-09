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

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.apache.log4j.Logger;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.interactions.base.AbstractStoreAuctionBuyNow;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class StoreAuctionBuyNow
        extends AbstractStoreAuctionBuyNow<EntityManager>
        implements Interaction<EntityManager> {
    private static final Logger LOGGER = Logger
            .getLogger(StoreAuctionBuyNow.class);

    @Override
    public Result execute(Param param, EntityManager em, ResultBuilder res) {

        BuyNowAuction bna = createBna(param);
        em.getTransaction().begin();
        Result execResult = null;
        try {
            execResult = store(bna, em, res);
            return execResult;
        } finally {
            if (em.getTransaction().isActive()) {
                if (execResult != null && execResult.isSuccess()) {
                    em.getTransaction().commit();
                } else {
                    em.getTransaction().rollback();
                }
            }
        }
    }

    private Result store(BuyNowAuction bna, EntityManager em,
            ResultBuilder res) {
        AuctionItem item = em.find(AuctionItem.class, bna.getItemId(),
                LockModeType.PESSIMISTIC_WRITE);
        Result checkResult = check(bna, item, res);
        if (checkResult.isSuccess()) {
            AuctionItem.sell(item);
            try {
                em.persist(bna);
                em.getTransaction().commit();
            } catch (EntityExistsException ex) {
                return res.warn("auction is not sold but BNA exists: "
                        + bna.getItemId())
                        .failure(ResultMode.UNKNOWN_ERROR);
            } catch (Exception ex) {
                LOGGER.error(
                        "unexpected error when inserting BNA", ex);

                return res.warn("unexpected exception: " + ex.getMessage())
                        .failure(ResultMode.UNKNOWN_ERROR);

            }
        }
        return checkResult;
    }

}
