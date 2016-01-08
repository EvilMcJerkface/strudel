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
package com.nec.strudel.workload.exec;

import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.instrument.impl.ProfilerServiceImpl;
import com.nec.strudel.management.ManagementService;
import com.nec.strudel.util.ClassUtil;
import com.nec.strudel.workload.exec.populate.PopulateWorkload;
import com.nec.strudel.workload.job.PopulateWorkItem;
import com.nec.strudel.workload.job.WorkItem;
import com.nec.strudel.workload.job.WorkRequest;
import com.nec.strudel.workload.job.WorkloadTask;
import com.nec.strudel.workload.out.Output;
import com.nec.strudel.workload.session.SessionWorkload;
import com.nec.strudel.workload.state.BatchWorkState;
import com.nec.strudel.workload.state.MeasureWorkState;
import com.nec.strudel.workload.state.WorkState;

public class WorkloadFactory {

	public static Output output(WorkloadTask workload) {
		return createWorkload(workload).output(workload.getParam());
	}
	private final WorkRequest work;


	public WorkloadFactory(WorkRequest work) {
		this.work = work;
	}


	public WorkExec createExec(ManagementService mx) {
		WorkItem item = work.getWorkItem();
		WorkState state = createWorkState(item);
		ProfilerServiceImpl profs = ProfilerServiceImpl.create(
					state.measurementState(), mx);
		Workload workload =
				createWorkload(work.getWorkItem());
		WorkExec exec = workload.createWorkExec(work.getConfig(),
				state, profs);
		exec.addCloseable(profs);
		return exec;
	}
	private WorkState createWorkState(WorkItem item) {
		if (item instanceof PopulateWorkItem) {
			return new BatchWorkState();
		} else {
			/**
			 * TODO make if configurable
			 */
			return new MeasureWorkState();
		}
	}

	@SuppressWarnings("rawtypes")
	protected static Workload createWorkload(WorkItem item) {
		if (item instanceof PopulateWorkItem) {
			return new PopulateWorkload();
		}
		String type = item.getType();
		if ("session".equals(type)) {
			return new SessionWorkload();
		} else if ("class".equals(type)) {
			String cname = item.getParam().getString("className");
			return ClassUtil.create(cname, item.getClassPath());
		} else {
			throw new ConfigException(
			"unknown workload: " + type);
		}
	}

}
