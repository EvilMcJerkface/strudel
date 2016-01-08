package com.nec.strudel.workload.server.rest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.nec.strudel.workload.server.WorkerService;

public class WorkerServiceRepository {
	private static final WorkerServiceRepository REPOSITORY =
			new WorkerServiceRepository();
	private final ConcurrentMap<String, WorkerService> services =
			new ConcurrentHashMap<String, WorkerService>();

	public static WorkerService getService(String name) {
		return REPOSITORY.get(name);
	}
	public static WorkerService registerService(
			String name, WorkerService service) {
		return REPOSITORY.set(name, service);
	}

	public WorkerService get(String name) {
		return services.get(name);
	}
	public WorkerService set(String name, WorkerService service) {
		return services.put(name, service);
	}
}
