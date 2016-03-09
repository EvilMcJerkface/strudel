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

package com.nec.strudel.bench.micro.interactions.base;

import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractGetItem<T> implements Interaction<T> {

    public enum InParam implements LocalParam {
        ITEM_ID,
    }

    /**
     * Gets an Item by ID which is specified as InParam.ITEM_ID. If the item is
     * found, set it to TransitionParam.ITEM. If it is not found, return
     * EMPTY_RESULT.
     */
    @Override
    public abstract Result execute(Param param, T db, ResultBuilder res);

    @Override
    public void prepare(ParamBuilder paramBuilder) {
        int userId = paramBuilder.getInt(SessionParam.USER_ID);
        int itemNo = paramBuilder.getRandomIntId(
                SessionParam.MIN_SEQ_NO,
                SessionParam.ITEMS_PER_USER);
        paramBuilder.set(InParam.ITEM_ID,
                new ItemId(userId, itemNo));
    }

    @Override
    public void complete(StateModifier modifier) {
        modifier.export(TransitionParam.ITEM);
    }

}