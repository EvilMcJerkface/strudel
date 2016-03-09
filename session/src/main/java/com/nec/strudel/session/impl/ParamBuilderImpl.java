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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.StateParam;
import com.nec.strudel.util.RandomSelector;

public class ParamBuilderImpl implements ParamBuilder {
    private final State state;
    private final Param param;

    public ParamBuilderImpl(State state, Param param) {
        this.state = state;
        this.param = param;
    }

    @Override
    public ParamBuilder use(StateParam paramName) {
        param.put(paramName, state.get(paramName));
        return this;
    }

    @Override
    public ParamBuilder use(LocalParam dst, StateParam src) {
        param.put(dst, state.get(src));
        return this;
    }

    @Override
    public boolean defined(StateParam paramName) {
        return state.get(paramName) != null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T get(StateParam paramName) {
        return (T) state.get(paramName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getList(StateParam paramName) {
        Object obj = state.get(paramName);
        if (obj == null) {
            return new ArrayList<T>();
        } else if (obj instanceof List) {
            return (List<T>) obj;
        } else if (obj instanceof Collection) {
            return new ArrayList<T>((Collection<T>) obj);
        } else {
            List<T> list = new ArrayList<T>();
            list.add((T) obj);
            return list;
        }
    }

    @Override
    public ParamBuilder set(LocalParam paramName, Object value) {
        param.put(paramName, value);
        return this;
    }

    @Override
    public ParamBuilder randomInt(LocalParam name,
            StateParam minName, StateParam maxName) {
        param.put(name, getRandomInt(minName, maxName));
        return this;
    }

    /**
     * Defines a random integer ID
     * 
     * @param name
     *            the parameter to be defined
     * @param minName
     *            the minimum ID
     * @param sizeName
     *            the size of (consecutive) ID numbers.
     * @param excludeName
     *            the ID to be excluded
     * @return this
     */
    @Override
    public ParamBuilder randomIntId(LocalParam name,
            StateParam minName, StateParam sizeName,
            StateParam excludeName) {
        param.put(name, getRandomIntId(minName, sizeName, excludeName));
        return this;
    }

    /**
     * Defines a random integer ID
     * 
     * @param name
     *            the parameter to be defined
     * @param minName
     *            the minimum ID
     * @param sizeName
     *            the size of (consecutive) ID numbers.
     * @param excludeName
     *            the ID to be excluded
     * @return this
     */
    @Override
    public ParamBuilder randomIntId(LocalParam name,
            StateParam minName, StateParam sizeName) {
        param.put(name, getRandomIntId(minName, sizeName));
        return this;
    }

    @Override
    public int getRandomIntId(StateParam minName, StateParam sizeName,
            StateParam excludeName) {
        int exclude = getInt(excludeName);
        int min = getInt(minName);
        int size = getInt(sizeName);
        int max = min + size;
        if (size == 1 && min == exclude) {
            throw new RuntimeException(
                    "invalid randomInt:[" + min
                            + "," + max + ") excluding " + exclude);
        } else if (size <= 0) {
            throw new RuntimeException(
                    "invalid randomInt:[" + min
                            + "," + max + ")");
        }
        int value = 0;
        RandomSelector<Integer> ints = RandomSelector.create(
                min, max);
        do {
            value = ints.next(state.getRandom());
        } while (exclude == value);
        return value;
    }

    @Override
    public int getRandomIntId(StateParam minName, StateParam sizeName) {
        int min = getInt(minName);
        int size = getInt(sizeName);
        int max = min + size;
        if (size <= 0) {
            throw new RuntimeException(
                    "invalid randomInt:[" + min
                            + "," + max + ")");
        }
        return getRandomInt(min, max);
    }

    @Override
    public Set<Integer> getRandomIntIdSet(StateParam countName,
            StateParam minName, StateParam sizeName) {
        int count = getInt(countName);
        int min = getInt(minName);
        int size = getInt(sizeName);
        int max = min + size;
        if (size < count) {
            throw new RuntimeException(
                    "invalid randomIntSet("
                            + count + "):[" + min
                            + "," + max + ")");
        }
        Set<Integer> set = new HashSet<Integer>();
        if (count > size / 2) {
            for (int i = min; i < max; i++) {
                set.add(i);
            }
            while (set.size() > count) {
                set.remove(getRandomInt(min, max));
            }
        } else {
            while (set.size() < count) {
                set.add(getRandomInt(min, max));
            }
        }
        return set;
    }

    @Override
    public ParamBuilder randomDouble(LocalParam name,
            StateParam minName, StateParam maxName) {
        param.put(name, getRandomDouble(minName, maxName));
        return this;
    }

    @Override
    public ParamBuilder randomAlphaString(LocalParam paramName, int length) {
        param.put(paramName, getRandomAlphaString(length));
        return this;
    }

    @Override
    public ParamBuilder randomAlphaString(LocalParam paramName,
            StateParam lengthParam) {
        return randomAlphaString(paramName, getInt(lengthParam));
    }

    @Override
    public int getInt(StateParam pname) {
        return state.getInt(pname);
    }

    @Override
    public double getDouble(StateParam name) {
        return state.getDouble(name);
    }

    @Override
    public int getRandomInt(StateParam minName, StateParam maxName) {
        return getRandomInt(getInt(minName), getInt(maxName));
    }

    @Override
    public int getRandomInt(int min, int max) {
        return RandomSelector.create(min, max).next(state.getRandom());
    }

    @Override
    public int getRandomInt(int max) {
        return state.getRandom().nextInt(max);
    }

    @Override
    public double getRandomDouble(StateParam minName, StateParam maxName) {
        return getRandomDouble(getDouble(minName), getDouble(maxName));
    }

    @Override
    public double getRandomDouble(double min, double max) {
        return RandomSelector.createDouble(min, max).next(state.getRandom());
    }

    @Override
    public double getRandomDouble() {
        return state.getRandom().nextDouble();
    }

    @Override
    public String getRandomAlphaString(int length) {
        return RandomSelector.createAlphaString(length).next(state.getRandom());
    }

}
