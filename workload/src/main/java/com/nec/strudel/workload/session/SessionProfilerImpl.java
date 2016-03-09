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

import javax.annotation.concurrent.NotThreadSafe;

import com.nec.strudel.instrument.Instrument;
import com.nec.strudel.instrument.TimeInstrument;
import com.nec.strudel.json.func.Div;
import com.nec.strudel.json.func.Sum;
import com.nec.strudel.json.func.Value;
import com.nec.strudel.metrics.Output;
import com.nec.strudel.metrics.TimeMetrics;
import com.nec.strudel.session.Result;
import com.nec.strudel.workload.exec.ReportNames;
import com.nec.strudel.workload.session.runner.SessionStatMonitor;

@NotThreadSafe
public class SessionProfilerImpl implements SessionProfiler {
    public static final String PROFILE_NAME = "session";
    public static final String INTERACTION = "interaction";
    public static final String COUNT_TIMES = TimeMetrics.timeOf(INTERACTION);
    public static final String COUNT_COUNTS = TimeMetrics.countOf(INTERACTION);

    public static final String INTERACTION_PER_SEC = "interaction_per_sec";
    public static final String AVG_EXEC_TIME = "average_exec_time";
    public static final String COUNT_AVG_TIMES = "interaction_avg_exec_time";
    private static final Output OUTPUT = Output.builder()
            .add(INTERACTION_PER_SEC,
                    Div.of(
                            Sum.of(Value.of(COUNT_COUNTS)),
                            Value.of(ReportNames.VALUE_MEASURE)))

            .add(AVG_EXEC_TIME,
                    Div.of(
                            Sum.of(Value.of(COUNT_TIMES)),
                            Sum.of(Value.of(COUNT_COUNTS))))

            .add(new TimeMetrics(INTERACTION)
                    .avg(COUNT_AVG_TIMES).outputs())
            .build();

    @Instrument
    private TimeInstrument interaction;
    private SessionStatMonitor mon;

    public SessionProfilerImpl() {
    }

    public TimeInstrument getInteraction() {
        return interaction;
    }

    public void setInteraction(TimeInstrument interaction) {
        this.interaction = interaction;
    }

    public void setMon(SessionStatMonitor mon) {
        this.mon = mon;
    }

    @Override
    public void newSession() {
        mon.newSession();
    }

    @Override
    public void startInteraction(String name) {
        interaction.start(name);
    }

    @Override
    public void finishInteraction(Result result) {
        long micro = interaction.end();
        if (micro > 0) {
            mon.interaction(micro, result.isSuccess());
        }
    }

    public static Output output() {
        return OUTPUT;
    }

    public static SessionProfiler noProfile() {
        return new SessionProfiler() {
            @Override
            public void startInteraction(String name) {
            }

            @Override
            public void newSession() {
            }

            @Override
            public void finishInteraction(Result result) {
            }
        };
    }
}