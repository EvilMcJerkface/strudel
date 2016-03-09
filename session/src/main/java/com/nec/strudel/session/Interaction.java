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

import javax.annotation.concurrent.ThreadSafe;

/**
 * An interaction that is executed in a session. The name of an interaction is
 * specified either
 * <ul>
 * <li>By convention: InteractionClassName has name INTERACTION_CLASS_NAME
 * <li>By annotation: use annotation Name
 * </ul>
 * 
 * @author tatemura
 *
 * @param <T>
 */
@ThreadSafe
public interface Interaction<T> {

    /**
     * Populates parameter. In a normal execution case, this parameter is used
     * for execution.
     * <p>
     * In order to create any random parameter, use the random object from
     * state.getRandom(). Do not use other instance of random objects. In a
     * debugging mode, the system may invoke a session with a random object with
     * a specific seed so that a single thread execution is repeatable even if
     * parameters are generated randomly.
     * 
     * @param paramBuilder
     *            the builder of the parameter based on the current state and
     *            randomness.
     */
    void prepare(ParamBuilder paramBuilder);

    /**
     * Executes the interaction. An execution should not use any random objects.
     * The behavior must be deterministic for the same parameter and the same
     * data in the database.
     * 
     * @param param
     *            the input parameter
     * @param db
     *            the database to access
     * @param res
     *            the result builder to build the result
     * @return result that is built.
     */
    Result execute(Param param, T db, ResultBuilder res);

    /**
     * Handles the result. Typically, the interaction changes the session state
     * based on the result.
     * 
     * @param modifier
     *            the modifier to change the state based on the result.
     */
    void complete(StateModifier modifier);
}
