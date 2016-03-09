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

import com.nec.strudel.bench.micro.entity.Submission;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractListSubmissionsToMe<T> implements Interaction<T> {

    public enum OutParam implements LocalParam {
        SUBMISSION_LIST
    }

    @Override
    public void prepare(ParamBuilder paramBuilder) {
        paramBuilder.use(SessionParam.USER_ID);
    }

    @Override
    public void complete(StateModifier modifier) {
        Submission sub = modifier.getOne(OutParam.SUBMISSION_LIST);
        if (sub != null) {
            modifier.set(TransitionParam.PEER_USER_ID,
                    sub.getSenderId());
        }
    }

}