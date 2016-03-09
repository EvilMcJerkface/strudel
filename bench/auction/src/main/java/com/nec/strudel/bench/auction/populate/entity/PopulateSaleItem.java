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

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.populate.SaleParamSet;
import com.nec.strudel.bench.auction.populate.SaleParamSet.SaleParam;
import com.nec.strudel.bench.auction.populate.base.AbstractPopulateSaleItem;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.workload.api.Populator;

public class PopulateSaleItem extends AbstractPopulateSaleItem<EntityDB>
        implements Populator<EntityDB, SaleParamSet> {
    @Override
    public void process(EntityDB edb, SaleParamSet sps) {
        int sellerId = sps.sellerId();
        for (SaleParam p : sps.getParams()) {
            SaleItem item = new SaleItem(sellerId);
            p.build(item);
            edb.create(item);
            edb.run(item,
                    populateGroup(p, item));
        }
    }

    /**
     * Populate entities that belongs to one entity group associated with a sale
     * item
     * 
     * @return the entity task that populates entities.
     */
    public EntityTask<List<BuyNowSale>> populateGroup(
            final SaleParam param, final SaleItem item) {
        final int buyNowNum = param.numOfBns();
        return new EntityTask<List<BuyNowSale>>() {
            @Override
            public List<BuyNowSale> run(EntityTransaction tx) {
                List<BuyNowSale> bnsList = new ArrayList<BuyNowSale>(buyNowNum);
                for (int i = 1; i <= buyNowNum; i++) {
                    bnsList.add(new BuyNowSale(item.getId()));
                }
                param.build(bnsList);
                for (BuyNowSale bns : bnsList) {
                    tx.create(bns);
                }
                return bnsList;
            }
        };
    }

}
