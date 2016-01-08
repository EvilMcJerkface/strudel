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
