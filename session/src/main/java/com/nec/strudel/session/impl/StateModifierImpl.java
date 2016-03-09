/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
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

package com.nec.strudel.session.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.nec.strudel.session.ParamName;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.StateModifier;
import com.nec.strudel.session.StateParam;
import com.nec.strudel.util.RandomUtil;

public class StateModifierImpl implements StateModifier {
    private final State state;
    private final Result res;

    public StateModifierImpl(Result res, State state) {
        this.res = res;
        this.state = state;
    }

    @Override
    public StateModifier export(StateParam paramName) {
        state.put(paramName, res.get(paramName));
        return this;
    }

    @Override
    public StateModifier set(StateParam name, Object value) {
        state.put(name, value);
        return this;
    }

    @Override
    public StateModifier choose(StateParam paramName, ParamName listName) {
        List<?> list = (List<?>) res.get(listName);
        if (list != null && !list.isEmpty()) {
            int randIdx = state.getRandom()
                    .nextInt(list.size());
            state.put(paramName, list.get(randIdx));
        }
        return this;
    }

    @Override
    public StateModifier chooseSubset(StateParam paramName,
            StateParam sizeName, ParamName listName) {
        List<?> list = (List<?>) res.get(listName);
        if (list != null && !list.isEmpty()) {
            int outSize = state.getInt(sizeName);
            state.put(paramName, chooseList(outSize, list));
        }
        return this;
    }

    private <T> List<T> chooseList(int size, List<T> source) {
        if (source.size() <= size) {
            return source;
        }
        ArrayList<T> result = new ArrayList<T>(source);
        RandomUtil.permutate(state.getRandom(), result);
        return result.subList(0, size);
    }

    @Nullable
    @Override
    public Object get(ParamName name) {
        return res.get(name);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getOne(ParamName listName) {
        List<?> list = (List<?>) res.get(listName);
        if (list != null && !list.isEmpty()) {
            int randIdx = state.getRandom()
                    .nextInt(list.size());
            return (T) list.get(randIdx);
        }
        return null;
    }

    @Override
    public boolean isSuccess() {
        return res.isSuccess();
    }
}
