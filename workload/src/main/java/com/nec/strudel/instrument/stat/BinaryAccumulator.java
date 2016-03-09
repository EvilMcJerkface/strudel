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

package com.nec.strudel.instrument.stat;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class BinaryAccumulator {
    private final AtomicLong trueCount = new AtomicLong(0);
    private final AtomicLong falseCount = new AtomicLong(0);

    public void event(boolean mode) {
        if (mode) {
            trueCount.incrementAndGet();
        } else {
            falseCount.incrementAndGet();
        }
    }

    public long getTrueCount() {
        return trueCount.get();
    }

    public long getFalseCount() {
        return falseCount.get();
    }
}