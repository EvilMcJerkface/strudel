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

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.nec.strudel.session.impl.State;

/**
 * A Session generates a sequence of interactions.
 * 
 * @author tatemura
 *
 */
@NotThreadSafe
public interface Session<T> {

    /**
     * Identifies what to do next.
     * 
     * @param state
     *            the current state of the session
     * @return the next interaction to execute; null if the session ends.
     */
    @Nullable
    UserAction<T> next(State state);

}
