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

package com.nec.strudel.bench.micro.interactions.jpa;

import javax.persistence.EntityManager;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.interactions.base.AbstractCreateShared;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class CreateShared extends AbstractCreateShared<EntityManager>
        implements Interaction<EntityManager> {

    @Override
    public Result execute(Param param, EntityManager em, ResultBuilder res) {
        int setId = param.getInt(InParam.SET_ID);
        String content = param.get(InParam.CONTENT);

        Shared item = new Shared(setId);
        item.setContent(content);
        em.getTransaction().begin();
        em.persist(item);
        em.getTransaction().commit();
        return res.success();
    }

}
