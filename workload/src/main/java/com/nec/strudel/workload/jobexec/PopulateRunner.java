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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.nec.strudel.exceptions.WorkloadException;
import com.nec.strudel.metrics.Report;
import com.nec.strudel.target.DatabaseCreator;
import com.nec.strudel.target.TargetConfig;
import com.nec.strudel.target.TargetLifecycle;
import com.nec.strudel.target.impl.DatabaseConfig;
import com.nec.strudel.target.impl.TargetFactory;
import com.nec.strudel.workload.cluster.Cluster;
import com.nec.strudel.workload.cluster.Node;
import com.nec.strudel.workload.job.Job;
import com.nec.strudel.workload.job.PopulateTask;
import com.nec.strudel.workload.job.PopulateWorkItem;
import com.nec.strudel.workload.job.WorkRequest;
import com.nec.strudel.workload.state.BatchWorkState;
import com.nec.strudel.workload.util.TimeUtil;
import com.nec.strudel.workload.worker.WorkGroup;

public class PopulateRunner implements Runnable {
	private static final Logger LOGGER =
	    Logger.getLogger(PopulateRunner.class);
	private final PopulateTask pop;
	private final TargetConfig dbConf;
	private final PopulateExec exe;

	public static PopulateRunner create(PopulateTask pop, Job job) {
		DatabaseConfig dbConf = pop.findDb();
		if (dbConf == null) {
			dbConf = job.createDb();
		}
		/**
		 * NOTE DatabaseConfig needs to know
		 * the class path to locate metadada
		 * (e.g., persistence.xml for JPA) in
		 * the workload package. 
		 */
		dbConf.setContextClassPath(pop.getClassPath());

		return new PopulateRunner(pop, dbConf,
				createExec(pop, dbConf, job));
	}

	private static PopulateExec createExec(PopulateTask pop, DatabaseConfig dbConf, Job job) {
		Cluster cc = findCluster(pop, job);
		if (cc != null) {
			return new ExecImpl(dbConf, cc.nodes());
		} else {
			return new ExecImpl(dbConf);
		}
	}

	protected static Cluster findCluster(PopulateTask pop, Job job) {
		Cluster cc = pop.findCluster();
		if (cc != null) {
			return cc;
		} else {
			cc = job.createCluster();
			if (cc.size() == 0) {
				return null;
			}
			int nodeNum = pop.getNodeNum();
			if (nodeNum > 0) {
				return cc.limit(nodeNum);
			} else {
				return null;
			}
		}
	}

	/**
	 * For testing (to plug-in PoplateExec)
	 */
	public static <T> PopulateRunner create(PopulateTask pop,
			TargetConfig dbConf,
			PopulateExec exec) {
		return new PopulateRunner(pop,
				dbConf,
				exec);
	}

	protected PopulateRunner(PopulateTask pop,
			TargetConfig dbConf, PopulateExec exec) {
		this.pop = pop;
		this.dbConf = dbConf;
		this.exe = exec;
	}

	public TargetConfig getTargetConfig() {
		return dbConf;
	}

	@Override
	public void run() {
		TargetLifecycle lcm = lifecycleManagement();
		long startTime = System.currentTimeMillis();
		try {
			lcm.operate(DatabaseCreator.INIT);
			for (PopulateWorkItem w : pop.getWorkItems()) {
				LOGGER.info("populate task: " + w.getName()
						+ " " + w.getIdRange());
				exe.execute(w);
			}
			lcm.operate(DatabaseCreator.PREPARE);
		} finally {
			exe.close();
			lcm.close();
		}
		long duration = System.currentTimeMillis() - startTime;
		LOGGER.info("populate tasks done in "
				+ TimeUtil.formatTimeMS(duration));
	}
	TargetLifecycle lifecycleManagement() {
		if (dbConf != null) {
			return TargetFactory.lifecycleManager(dbConf);
		} else {
			return DatabaseCreator.NULL_CREATOR;
		}
	}
	

	public interface PopulateExec {
		void execute(PopulateWorkItem wxml);
		void close();
	}
	static class ExecImpl implements PopulateExec {
		private final Node[] nodes;
		private final DatabaseConfig dbConf;
		public ExecImpl(DatabaseConfig dbConf,
				Node ... nodes) {
			this.dbConf = dbConf;
			this.nodes = nodes;
		}
		public ExecImpl(DatabaseConfig dbConf) {
			this(dbConf, Node.empty());
		}
		@Override
		public void execute(PopulateWorkItem item) {
			WorkRequest[] works = new WorkRequest[nodes.length];
			for (int i = 0; i < works.length; i++) {
				works[i] = new WorkRequest(nodes[i], item, dbConf);
			}
			WorkGroup wg = WorkGroup.create(works);
			try {
				wg.start();
				waitCompletion(item, wg);
				wg.terminate();
			} catch (InterruptedException e) {
				throw new WorkloadException("interrupted", e);
			} finally {
				wg.close();
			}
		}
		private static final int SLEEP_INTVL = 1000;

		void waitCompletion(PopulateWorkItem item,
				WorkGroup wg) throws InterruptedException {
			int size = wg.size();
			int done = 0;
			List<String> states = Arrays.asList();
			while (done < size) {
				Thread.sleep(SLEEP_INTVL);
				states = wg.getStates();
				int newDone = doneSize(states);
				if (newDone > done) {
					done = newDone;
					LOGGER.info(done
						+ "/" + size + " done");
				}
			}
			List<String> failed = new ArrayList<String>();
			for (int i = 0; i < states.size(); i++) {
				String s = states.get(i);
				if (BatchWorkState.isError(s)) {
					Node node = nodes[i];
					failed.add(node.getUrl());
				}
			}
			if (!failed.isEmpty()) {
				throw new WorkloadException(
				"Populator execution failed at "
					+ failed + " for task "
					+ item.getName());
			}
			Report rep = wg.getReport();
			for (String message : rep.getWarns()) {
				LOGGER.warn(message);
			}
		}
		int doneSize(List<String> states) {
			int count = 0;
			for (String s : states) {
				if (BatchWorkState.isDone(s)) {
					count += 1;
				} else if (BatchWorkState.isError(s)) {
					count += 1;
				}
			}
			return count;
		}

		@Override
		public void close() {
		}
	}
}
