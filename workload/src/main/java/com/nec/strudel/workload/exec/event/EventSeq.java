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

package com.nec.strudel.workload.exec.event;

import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * A generator of event time sequence.
 * 
 * @author tatemura
 *
 * @param <R>
 *            the type of event result.
 */
@NotThreadSafe
public interface EventSeq<R> {

    /**
     * Gets the set of timed events driven by the completion of an event.
     * 
     * @param res
     *            the result of the completed event.
     * @return empty if there is no event driven by the given result.
     */
    Collection<TimedEvent<R>> next(R res);

    /**
     * Gets the initial set of timed events.
     */
    Collection<TimedEvent<R>> start();

    /**
     * Polls to check if there are new timed events. The executor will call this
     * periodically (at least once per second).
     * <p>
     * A typical use case is to represent arrival of new users (to initiate new
     * sessions).
     * 
     * @return an empty collection if there is no new timed event.
     */
    Collection<TimedEvent<R>> poll();
}
