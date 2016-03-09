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

package com.nec.strudel.instrument.impl;

import java.util.concurrent.TimeUnit;

public class MeasurementTimer implements MeasurementState {
    private volatile long completeTime = Long.MAX_VALUE;
    private volatile boolean started = false;
    private volatile boolean completed = false;

    public void start(int seconds) {
        this.started = true;
        this.completeTime = System.currentTimeMillis()
                + TimeUnit.SECONDS.toMillis(seconds);
    }

    public boolean isDone() {
        return System.currentTimeMillis() >= completeTime;
    }

    @Override
    public boolean isMeasuring() {
        if (!started) {
            return false;
        }
        if (completed) {
            return false;
        } else if (isDone()) {
            completed = true;
            return false;
        }
        return true;
    }

}