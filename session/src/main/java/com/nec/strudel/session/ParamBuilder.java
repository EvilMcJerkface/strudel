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

package com.nec.strudel.session;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

public interface ParamBuilder {

    ParamBuilder use(StateParam paramName);

    ParamBuilder use(LocalParam dstName, StateParam srcName);

    boolean defined(StateParam paramName);

    @Nullable
    <T> T get(StateParam paramName);

    <T> List<T> getList(StateParam paramName);

    ParamBuilder set(LocalParam paramName, Object value);

    ParamBuilder randomInt(LocalParam name, StateParam minName,
            StateParam maxName);

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
    ParamBuilder randomIntId(LocalParam name, StateParam minName,
            StateParam sizeName, StateParam excludeName);

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
    ParamBuilder randomIntId(LocalParam name, StateParam minName,
            StateParam sizeName);

    int getRandomIntId(StateParam minName, StateParam sizeName,
            StateParam excludeName);

    int getRandomIntId(StateParam minName, StateParam sizeName);

    Set<Integer> getRandomIntIdSet(StateParam countName, StateParam minName,
            StateParam sizeName);

    ParamBuilder randomDouble(LocalParam name, StateParam minName,
            StateParam maxName);

    ParamBuilder randomAlphaString(LocalParam paramName, int length);

    ParamBuilder randomAlphaString(LocalParam paramName, StateParam lengthParam);

    int getInt(StateParam pname);

    double getDouble(StateParam name);

    int getRandomInt(StateParam minName, StateParam maxName);

    int getRandomInt(int max);

    int getRandomInt(int min, int max);

    double getRandomDouble(StateParam minName, StateParam maxName);

    double getRandomDouble(double min, double max);

    double getRandomDouble();

    String getRandomAlphaString(int length);

}