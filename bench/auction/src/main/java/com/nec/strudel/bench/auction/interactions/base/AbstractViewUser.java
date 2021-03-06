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

package com.nec.strudel.bench.auction.interactions.base;

import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractViewUser<T> implements Interaction<T> {

    public enum OutParam implements LocalParam {
        USER
    }

    @Override
    public void prepare(ParamBuilder builder) {
        builder.use(SessionParam.USER_ID);
    }

    @Override
    public void complete(StateModifier modifier) {
        // do nothing
    }

    public int getUserId(Param param) {
        return param.getInt(SessionParam.USER_ID);
    }

    public Result resultOf(User user, Param param, ResultBuilder res) {
        if (user == null) {
            int userId = param.getInt(SessionParam.USER_ID);
            res.warn("user not found: id=" + userId);
        }
        res.set(OutParam.USER, user);
        return res.success();
    }

}