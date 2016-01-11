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
package com.nec.strudel.workload.jobexec;

import javax.annotation.concurrent.NotThreadSafe;
import javax.json.JsonObject;

import org.apache.log4j.Logger;

import com.nec.strudel.exceptions.WorkloadException;
import com.nec.strudel.metrics.Output;
import com.nec.strudel.metrics.ProfilerValue;
import com.nec.strudel.target.impl.DatabaseConfig;
import com.nec.strudel.target.impl.TargetFactory;
import com.nec.strudel.workload.cluster.Cluster;
import com.nec.strudel.workload.cluster.Node;
import com.nec.strudel.workload.com.Command;
import com.nec.strudel.workload.com.CommandResult;
import com.nec.strudel.workload.exec.WorkloadFactory;
import com.nec.strudel.workload.job.Job;
import com.nec.strudel.workload.job.WorkRequest;
import com.nec.strudel.workload.job.WorkloadTask;
import com.nec.strudel.workload.jobexec.com.OutputFinder;
import com.nec.strudel.workload.out.OutputConfig;
import com.nec.strudel.workload.out.OutputSet;
import com.nec.strudel.workload.worker.WorkGroup;

@NotThreadSafe
public class WorkloadRunner implements Runnable {
	private static final Logger LOGGER =
	    Logger.getLogger(WorkloadRunner.class);

	private final WorkloadTask workloadTask;
	private final Cluster cc;
	private final DatabaseConfig dbConf;
	private final OutputConfig output;
	private final WorkloadProfile wp =
			new WorkloadProfile();
	private final Command com;

	public static Logger logger() {
		return LOGGER;
	}

	public WorkloadRunner(WorkloadTask work, Job job,
			Command command) {
		this.workloadTask = work;
		this.com = command;
		this.cc = job.createCluster();
		this.dbConf = job.createDb();
		this.output = job.createOutput();
		wp.setName(job.getName());
		wp.setJobInfo(job.getInfo());
	}

	public void run() {
	    WorkExecContext ctxt = createContext();
		try {
		    wp.setStartDateNow();
			WorkloadResult result = completeWork(ctxt, com);
			wp.setEndDateNow();
			report(com, result);
		} catch (InterruptedException e) {
			LOGGER.error("workload interrupted ", e);
		} finally {
			try {
				ctxt.terminate();
			} catch (InterruptedException e) {
				LOGGER.error(
					"workload shutdown interrupted ", e);
			}
			ctxt.close();
		}
	}
	private WorkloadResult completeWork(WorkExecContext ctxt, Command c)
			throws InterruptedException {
		CommandResult r = c.run(ctxt);
		if (!r.isSuccessful()) {
			throw new WorkloadException(
			"workload execution failed: "
			 + r.getMsg() + "\n" + r.getLog());
		}
		return ctxt.getResult();
	}
	private void report(Command c, WorkloadResult res) {
		output.warnings(res.getWarns());

		OutputSet out = OutputSet.builder()
				.add("header", wp.toOutput())
				.add("include", output.getInclude())
				.add(OutputFinder.output(c))
				.add(DatabaseConfig.TAG_NAME, 
						dbOutput())
				.add(WorkloadTask.TAG_NAME,
					WorkloadFactory.output(workloadTask))
				.build();

		JsonObject input = ProfilerValue.combine(
				wp.toJson(),
				res.getResult());
		output.output(out, input);
	}
	private Output dbOutput() {
		return Output.funcs(TargetFactory.outputs(dbConf));
	}

	private WorkExecContext createContext() {
		WorkGroup wg = createWorkGroup();
		return new WorkExecContext(wg, LOGGER);
	}
	private WorkGroup createWorkGroup() {
		int threadNum = workloadTask.numOfThreads();

		Node[] nodes = cc.nodes();
		int size = nodes.length;
		if (size == 0) {
			LOGGER.info("starting one local worker with "
					+ threadNum + " threads...");
			return WorkGroup.create(
					WorkRequest.createLocal(
					workloadTask, dbConf));
		}

		LOGGER.info("starting "
				+ size + (cc.isLocal() ? " local" : "")
				+ " nodes with "
				+ threadNum + " threads per node");
		WorkRequest[] works = new WorkRequest[size];
		for (int i = 0; i < size; i++) {
			works[i] =
				new WorkRequest(nodes[i], workloadTask, dbConf);
		}
		return WorkGroup.create(works);
	}


}
