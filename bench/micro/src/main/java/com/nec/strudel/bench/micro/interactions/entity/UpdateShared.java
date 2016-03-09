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

package com.nec.strudel.bench.micro.interactions.entity;

import java.util.List;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.entity.SharedId;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdateShared;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.entity.EntityTask;
import com.nec.strudel.entity.EntityTransaction;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;

public class UpdateShared extends AbstractUpdateShared<EntityDB>
        implements Interaction<EntityDB> {
    @Override
    public Result execute(Param param, EntityDB db, ResultBuilder res) {
        final List<SharedId> ids = param.getObjectList(
                InParam.SHARED_IDS);
        if (ids.isEmpty()) {
            return res.warn("SHARED_ID is not set")
                    .failure(ResultMode.MISSING_PARAM);
        }

        final String content = param.get(InParam.CONTENT);
        boolean updated = db.run(Shared.class, ids.get(0),
                new EntityTask<Boolean>() {
                    @Override
                    public Boolean run(EntityTransaction tx) {
                        boolean hasUpdate = false;
                        for (SharedId id : ids) {
                            Shared shared = tx.get(Shared.class, id);
                            if (shared != null) {
                                shared.setContent(content);
                                tx.update(shared);
                                hasUpdate = true;
                            }
                        }
                        return hasUpdate;
                    }
                });
        if (updated) {
            return res.success();
        } else {
            return res.success(ResultMode.EMPTY_RESULT);
        }
    }

}
