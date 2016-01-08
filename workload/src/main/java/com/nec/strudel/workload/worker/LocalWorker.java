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
package com.nec.strudel.workload.worker;

import java.util.concurrent.TimeUnit;

import javax.json.JsonObject;

import org.apache.log4j.Logger;

import com.nec.strudel.management.ManagementService;
import com.nec.strudel.management.RegistrationException;
import com.nec.strudel.management.jmx.JmxManagementService;
import com.nec.strudel.management.resource.Getter;
import com.nec.strudel.management.resource.ManagedResource;
import com.nec.strudel.management.resource.Operation;
import com.nec.strudel.management.resource.ResourceId;
import com.nec.strudel.workload.exec.Report;
import com.nec.strudel.workload.exec.WorkExec;
import com.nec.strudel.workload.exec.WorkloadFactory;
import com.nec.strudel.workload.job.WorkRequest;
import com.nec.strudel.workload.util.TimeValue;

@ManagedResource(description = "Worker that runs one work")
public class LocalWorker implements Worker {
	public static final long TERMINATION_TIMEOUT_SEC = 300;

	private static final Logger LOGGER =
		    Logger.getLogger(LocalWorker.class);
	private final String workId;
	private final WorkExec wexec;
	private final ManagementService mx;
	private final TimeValue slack;
	private final String type;

	public static LocalWorker create(String workId, WorkRequest work) {
		ManagementService mx =
				new JmxManagementService();
		LocalWorker w = new LocalWorker(workId, work, mx);
		mx.register(w);
		return w;
	}

	public LocalWorker(String workId, WorkRequest work,
			ManagementService mx) {
		this.workId = workId;
		this.wexec = new WorkloadFactory(work).createExec(mx);
		this.mx = mx;
		this.slack = work.startSlackTime();
		this.type = work.getType();
	}

	@Override
	@ResourceId
	public String getWorkId() {
		return workId;
	}

	@Override
	@Operation
	public synchronized void start() {
		wexec.start(slack);
	}

	@Override
	public void operate(String name, JsonObject data) {
		wexec.operate(name, data);
	}

	@Override
	public Report getReport() {
		return wexec.getReport();
	}

	@Override
	@Operation
	public void stop() {
		wexec.stop();
	}

	@Override
	@Operation
	public synchronized void terminate() throws InterruptedException {
		boolean done = wexec.terminate(
				TERMINATION_TIMEOUT_SEC, TimeUnit.SECONDS);
		if (!done) {
			LOGGER.error("work termination timeout");
			/**
			 * TODO exception
			 */
		}
		wexec.close();
		try {
			mx.unregister(this);
		} catch (RegistrationException e) {
			LOGGER.warn("Worker unregister failed. ignoring...", e);
		}
	}

	@Getter
	@Override
	public String getState() {
		return wexec.getState();
	}

	@Getter
	public long getStartSlackMS() {
		return slack.toMillis();
	}

	@Getter
	public int getThreadNum() {
		return wexec.numOfThreads();
	}
	@Getter
	public String getWorkloadType() {
		return type;
	}

}
