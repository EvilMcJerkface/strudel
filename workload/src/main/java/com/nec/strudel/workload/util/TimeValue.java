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

package com.nec.strudel.workload.util;

import java.util.concurrent.TimeUnit;

public class TimeValue {
    public static TimeValue seconds(long seconds) {
        return new TimeValue(seconds, TimeUnit.SECONDS);
    }

    /**
     * Converts double-value seconds (as small as 1 microsecond).
     * 
     * @param seconds
     */
    public static TimeValue seconds(double seconds) {
        long micro = TimeUnit.MICROSECONDS.convert(1, TimeUnit.SECONDS);
        return new TimeValue((long) (micro * seconds),
                TimeUnit.MICROSECONDS);
    }

    public static TimeValue milliseconds(long msec) {
        return new TimeValue(msec, TimeUnit.MILLISECONDS);
    }

    private long value;
    private TimeUnit unit;

    public TimeValue(long value, TimeUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public long getTime(TimeUnit unit) {
        return unit.convert(value, this.unit);
    }

    public long getValue() {
        return value;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public long toMillis() {
        return this.unit.toMillis(value);
    }

    public String toString() {
        long ms = toMillis();
        /**
         * TODO nanos?
         */
        return TimeUtil.formatTimeMs(ms);
    }

}
