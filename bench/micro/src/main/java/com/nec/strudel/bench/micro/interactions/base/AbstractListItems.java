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

import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractListItems<T> implements Interaction<T> {

    public enum OutParam implements LocalParam {
        ITEM_LIST
    }

    /**
     * Gets items by user ID (specified in the param as SessionParam.USER_ID).
     * Set the list of Item instances as OutParam.ITEM_LIST. If the list is
     * empty, return EMPTY_RESULT.
     */
    @Override
    public abstract Result execute(Param param, T db, ResultBuilder res);

    @Override
    public void prepare(ParamBuilder paramBuilder) {
        paramBuilder.use(SessionParam.USER_ID);
    }

    @Override
    public void complete(StateModifier modifier) {
        modifier.chooseSubset(TransitionParam.ITEM,
                SessionParam.NUM_UPDATE_ITEMS,
                OutParam.ITEM_LIST);
    }

}