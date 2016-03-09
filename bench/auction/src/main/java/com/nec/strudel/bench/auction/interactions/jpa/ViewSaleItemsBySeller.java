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

import java.util.List;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.interactions.base.AbstractViewSaleItemsBySeller;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class ViewSaleItemsBySeller
        extends AbstractViewSaleItemsBySeller<EntityManager>
        implements Interaction<EntityManager> {
    public static final String QUERY = "SELECT s FROM SaleItem s WHERE s.sellerId = :sid";
    public static final String PARAM = "sid";

    @Override
    public Result execute(Param param, EntityManager em, ResultBuilder res) {
        int sellerId = getSellerId(param);
        List<SaleItem> itemList = em.createQuery(QUERY, SaleItem.class)
                .setParameter(PARAM, sellerId)
                .getResultList();

        return resultOf(itemList, param, res);
    }

}
