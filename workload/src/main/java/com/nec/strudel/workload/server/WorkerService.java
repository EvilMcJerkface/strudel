package com.nec.strudel.workload.server;

import java.util.Set;

import javax.json.JsonObject;

import com.nec.strudel.workload.job.WorkRequest;

public interface WorkerService {

	WorkStatus init(WorkRequest work);
	WorkStatus start(String workId);
	WorkStatus stop(String workId);
	WorkStatus terminate(String workId) throws InterruptedException;

    WorkStatus getStatus(String workId);
    WorkStatus operate(String workId, String name, JsonObject data);
    JsonObject getReport(String workId);
    Set<String> works();
}
