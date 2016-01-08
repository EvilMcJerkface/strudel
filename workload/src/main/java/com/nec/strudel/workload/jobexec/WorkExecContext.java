package com.nec.strudel.workload.jobexec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.json.JsonObject;

import org.apache.log4j.Logger;

import com.nec.strudel.json.JsonValues;
import com.nec.strudel.workload.com.CommandContext;
import com.nec.strudel.workload.worker.WorkGroup;

public class WorkExecContext implements CommandContext {
	private WorkGroup wg;
	private final Logger logger;
	private List<WorkloadResult> results = new ArrayList<WorkloadResult>();

	public WorkExecContext(WorkGroup wg, Logger logger) {
		this.wg = wg;
		this.logger = logger;
	}

	@Override
	public <T> List<Future<T>> call(List<? extends Callable<T>> calls) {
		return wg.call(calls);
	}

	@Override
	public Logger logger() {
		return logger;
	}

	public WorkGroup workGroup() {
		return wg;
	}

	public synchronized void result(WorkloadResult res) {
		this.results.add(res);
	}

	public synchronized WorkloadResult getResult() {
		List<String> warns = new ArrayList<String>();
		List<JsonObject> values = new ArrayList<JsonObject>();
		for (WorkloadResult r : results) {
			values.add(r.getResult());
			warns.addAll(r.getWarns());
		}
		return new WorkloadResult(
				JsonValues.union(values), warns);
	}
	public void terminate() throws InterruptedException {
		wg.terminate();
	}
	public void close() {
		wg.close();
	}

}
