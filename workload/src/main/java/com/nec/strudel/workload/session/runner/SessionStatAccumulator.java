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

package com.nec.strudel.workload.session.runner;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.instrument.stat.BinaryAccumulator;
import com.nec.strudel.instrument.stat.LongAccumulator;

@ThreadSafe
public class SessionStatAccumulator {
    private final LongAccumulator interaction = new LongAccumulator();
    private final AtomicInteger newSession = new AtomicInteger();
    private final BinaryAccumulator interactionSuccess = new BinaryAccumulator();

    public SessionStatAccumulator() {
    }

    public void interaction(long microSec) {
        interaction.event(microSec);
    }

    public void interactionResult(boolean success) {
        interactionSuccess.event(success);
    }

    public long getInteractionCount() {
        return interaction.count();
    }

    public long getInteractionTime() {
        return interaction.sum();
    }

    public void newSession() {
        newSession.incrementAndGet();
    }

    public int getNewSessionCount() {
        return newSession.get();
    }

    public long getSuccessCount() {
        return interactionSuccess.getTrueCount();
    }

    public long getFailureCount() {
        return interactionSuccess.getFalseCount();
    }
}
