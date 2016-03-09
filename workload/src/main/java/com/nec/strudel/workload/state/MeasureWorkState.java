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

package com.nec.strudel.workload.state;

import javax.json.JsonObject;

import org.apache.log4j.Logger;

import com.nec.strudel.instrument.impl.MeasurementState;
import com.nec.strudel.instrument.impl.MeasurementTimer;

public class MeasureWorkState implements WorkState {
    private static final Logger LOGGER = Logger
            .getLogger(MeasureWorkState.class);

    enum State {
        INIT, START, MEASURE, DONE, STOP, TERMINATE, ERROR;

        public String getLabel() {
            return this.name().toLowerCase();
        }

        public boolean isRunning() {
            return this == State.DONE
                    || this == State.MEASURE
                    || this == State.START;
        }

    }

    public static final String COMMAND_MEASURE = State.MEASURE.getLabel();
    public static final String VALUE_MEASURE = "measure";

    private State state = State.INIT;
    private final MeasurementTimer timer;
    public static final String STATE_DONE = State.DONE.getLabel();

    public MeasureWorkState() {
        this.timer = new MeasurementTimer();
    }

    public MeasureWorkState(MeasurementTimer timer) {
        this.timer = timer;
    }

    @Override
    public MeasurementState measurementState() {
        return timer;
    }

    @Override
    public String getState() {
        if (state == State.MEASURE) {
            if (timer.isDone()) {
                LOGGER.info("measure done");
                state = State.DONE;
            }
        }
        return state.getLabel();
    }

    @Override
    public void fail() {
        if (state == State.MEASURE) {
            state = State.ERROR;
        }
    }

    @Override
    public void done() {
        /**
         * ignore: task is finished by timer.
         */
    }

    @Override
    public boolean start() {
        if (state == State.INIT) {
            state = State.START;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean stop() {
        if (this.isRunning()) {
            state = State.STOP;
            return true;
        }
        return false;
    }

    @Override
    public boolean terminate() {
        if (state == State.STOP) {
            state = State.TERMINATE;
            return true;
        }
        return false;
    }

    @Override
    public boolean operate(String name, JsonObject data) {
        if (COMMAND_MEASURE.equals(name)) {
            if (state == State.START) {
                state = State.MEASURE;
                startMeasure(data);
                LOGGER.info("measure started");
            } else if (state == State.MEASURE) {
                LOGGER.info(
                        "ignoring start measure:"
                                + " already started");
            } else {
                LOGGER.warn(
                        "cannot start measure from the state="
                                + state);
            }
            return true;
        }
        return false;
    }

    private void startMeasure(JsonObject data) {
        int seconds = data.getInt(
                VALUE_MEASURE);
        timer.start(seconds);
    }

    @Override
    public boolean isRunning() {
        return state.isRunning();
    }

}
