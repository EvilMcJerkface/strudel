package com.nec.strudel.workload.jobexec;

import java.util.Collection;
import java.util.Collections;

import javax.json.JsonObject;

public class WorkloadResult {

	private final Collection<String> warns;
	private final JsonObject result;
	public WorkloadResult(JsonObject result,
			Collection<String> warns) {
		this.warns = warns;
		this.result = result;
	}
	public WorkloadResult(JsonObject result) {
		this.warns = Collections.emptyList();
		this.result = result;
	}
	public JsonObject getResult() {
		return result;
	}
	public Collection<String> getWarns() {
		return warns;
	}

}