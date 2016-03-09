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

package com.nec.strudel.bench.micro.populate.entity;

import java.util.List;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.bench.micro.populate.base.AbstractPopulateItem;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.workload.api.Populator;

public class PopulateItem extends AbstractPopulateItem<EntityDB>
        implements Populator<EntityDB, ContentSet> {

    @Override
    public void process(EntityDB db, final ContentSet param) {
        final int userId = param.getGroupId();
        db.run(Item.class, userId, new EntityTask<Void>() {
            @Override
            public Void run(EntityTransaction tx) {
                for (String c : param.getContents()) {
                    Item item = new Item(userId);
                    item.setContent(c);
                    tx.create(item);
                }
                return null;
            }
        });
    }

    @Override
    protected List<Item> getItemsByUser(EntityDB db, int userId) {
        return db.getEntitiesByIndex(Item.class,
                "userId", userId);
    }

}
