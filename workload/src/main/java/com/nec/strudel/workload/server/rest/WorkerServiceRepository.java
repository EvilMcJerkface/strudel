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
