package com.nec.strudel.workload.worker;

import javax.json.JsonObject;

import com.nec.strudel.workload.exec.Report;

public interface Worker {

	String STATE_ERROR = "error";

	String getWorkId();

	void start();

	void stop();

	void terminate() throws InterruptedException;

	void operate(String name, JsonObject data);

	String getState();

    Report getReport();
}