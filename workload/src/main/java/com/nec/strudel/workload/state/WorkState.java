package com.nec.strudel.workload.state;

import javax.json.JsonObject;

import com.nec.strudel.instrument.impl.MeasurementState;

public interface WorkState {
	String getState();
	boolean start();
	boolean stop();
	boolean terminate();
	boolean operate(String name, JsonObject data);
	boolean isRunning();
	void fail();
	void done();
	MeasurementState measurementState();
}