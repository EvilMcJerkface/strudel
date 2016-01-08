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

import com.nec.strudel.instrument.impl.MeasurementState;

public class BatchWorkState implements WorkState {
	enum State {
		INITIALIZED,
		RUNNING,
		DONE,
		TERMINATED,
		ERROR;
		public String getLabel() {
			return this.name().toLowerCase();
		}
	}

	public static boolean isDone(String state) {
		return State.DONE.getLabel().equals(state);
	}
	public static boolean isError(String state) {
		return State.ERROR.getLabel().equals(state);
	}
	private State state = State.INITIALIZED;

	public BatchWorkState() {
	}

	@Override
	public MeasurementState measurementState() {
		return MeasurementState.ALWAYS;
	}
	@Override
	public String getState() {
		return state.getLabel();
	}
	@Override
	public void fail() {
		if (state == State.RUNNING) {
			state = State.ERROR;
		}
	}
	@Override
	public void done() {
		if (state == State.RUNNING) {
			state = State.DONE;
		}		
	}

	@Override
	public boolean start() {
		if (state == State.INITIALIZED) {
			state = State.RUNNING;
			return true;
		}
		return false;
	}

	@Override
	public boolean stop() {
		/**
		 * NOTE cannot stop...
		 */
		return false;
	}

	@Override
	public boolean terminate() {
		if (state == State.DONE || state == State.ERROR) {
			state = State.TERMINATED;
			return true;
		}
		return false;
	}

	@Override
	public boolean operate(String name, JsonObject data) {
		return false;
	}

	@Override
	public boolean isRunning() {
		return state == State.RUNNING;
	}


}
