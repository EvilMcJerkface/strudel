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

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.instrument.BinaryEventStat;

@ThreadSafe
public class BinaryEventMonitor extends SlidingWindow<BinaryAccumulator>
        implements BinaryEventStat {

    public BinaryEventMonitor(int windowSize, long stepMs) {
        super(windowSize, stepMs);
    }

    @Override
    protected BinaryAccumulator newInstance() {
        return new BinaryAccumulator();
    }

    @Override
    public void event(boolean mode) {
        getCurrent().event(mode);
    }

    @Override
    public double getTrueRatio() {
        Window<BinaryAccumulator>[] buffs = getPast();
        long trueCount = 0;
        long falseCount = 0;
        for (Window<BinaryAccumulator> w : buffs) {
            BinaryAccumulator bin = w.get();
            trueCount += bin.getTrueCount();
            falseCount += bin.getFalseCount();
        }
        if (falseCount == 0) {
            if (trueCount == 0) {
                return Double.NaN; // UNDEF
            }
            return 1;
        } else if (trueCount == 0) {
            return 0;
        } else {
            return ((double) trueCount) / (trueCount + falseCount);
        }
    }
}
