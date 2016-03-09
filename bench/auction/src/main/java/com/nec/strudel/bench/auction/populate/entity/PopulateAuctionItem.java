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

package com.nec.strudel.bench.auction.populate.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.log4j.Logger;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.populate.AuctionParamSet;
import com.nec.strudel.bench.auction.populate.AuctionParamSet.AuctionParam;
import com.nec.strudel.bench.auction.populate.base.AbstractPopulateAuctionItem;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.workload.api.Populator;

public class PopulateAuctionItem extends AbstractPopulateAuctionItem<EntityDB>
        implements Populator<EntityDB, AuctionParamSet> {
    private static final Logger LOGGER = Logger
            .getLogger(PopulateAuctionItem.class);

    @Override
    public void process(EntityDB edb, AuctionParamSet aps) {
        int sellerId = aps.sellerId();
        for (AuctionParam p : aps.getParams()) {
            AuctionItem item = new AuctionItem(sellerId);
            p.build(item);
            edb.create(item);
            BuyNowAuction bna = p.bnaIfExists(item);

            edb.run(item,
                    populateGroup(p, item, bna));
            /**
             * NOTE: when running in one large transaction (as in
             * populateGroup()), it causes conflict over indexes... Let's update
             * entities separately:
             *      create(edb, p, item, bna);
             */
        }
    }

    protected void create(EntityDB edb, AuctionParam param, AuctionItem item,
            @Nullable BuyNowAuction bna) {
        int totalBids = param.numOfBids();
        LOGGER.debug("populating " + totalBids
                + " bids on items:" + item.getItemId());
        List<Bid> bids = new ArrayList<Bid>(totalBids);
        for (int i = 1; i <= totalBids; i++) {
            bids.add(new Bid(item.getItemId()));
        }
        param.build(bids);
        for (Bid bid : bids) {
            LOGGER.debug("bid: " + bid);
            edb.create(bid);
        }
        if (bna != null) {
            edb.create(bna);
        }

    }

    /**
     * Populates entities in the group with the given item (including the item
     * itself)
     * 
     * @param param
     * @param item
     * @param bna
     * @return populated Bid and BNA.
     */
    public EntityTask<List<Bid>> populateGroup(
            final AuctionParam param, final AuctionItem item,
            @Nullable final BuyNowAuction bna) {
        final int totalBids = param.numOfBids();
        return new EntityTask<List<Bid>>() {
            @Override
            public List<Bid> run(EntityTransaction tx) {
                List<Bid> bids = new ArrayList<Bid>(totalBids);
                for (int i = 1; i <= totalBids; i++) {
                    bids.add(new Bid(item.getItemId()));
                }
                param.build(bids);
                for (Bid bid : bids) {
                    tx.create(bid);
                }
                if (bna != null) {
                    tx.create(bna);
                }
                return bids;
            }
        };
    }

    @Override
    protected List<AuctionItem> getItemsBySeller(EntityDB db, int sellerId) {
        return db.getEntitiesByIndex(
                AuctionItem.class,
                "sellerId", sellerId);
    }

    @Override
    protected AuctionItem getItem(EntityDB db, ItemId id) {
        return db.get(
                AuctionItem.class, id);
    }

    @Override
    protected BuyNowAuction getBuyNowAuction(EntityDB db, ItemId id) {
        return db.get(BuyNowAuction.class, id);
    }

    @Override
    protected List<Bid> getBidsByItemId(EntityDB db, ItemId id) {
        return db.getEntitiesByIndex(Bid.class,
                "auctionItemId", id);
    }

}
