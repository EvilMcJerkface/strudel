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
package com.nec.strudel.workload.session;

import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import com.nec.strudel.param.ParamSequence;
import com.nec.strudel.session.impl.State;

public class StateFactory {
    private final Random rand;
    private final ParamSequence params;
    public StateFactory(ParamSequence params, Random rand) {
        this.params = params;
        this.rand = rand;
    }
    @Nullable
    public State next() {
        Map<String, Object> param = params.nextParam(rand);
        if (param != null) {
            return State.newState(param, rand);
        } else {
            return null;
        }
    }
    public Random getRandom() {
    	return rand;
    }
}