package com.nec.strudel.workload.server;

import javax.json.JsonObject;

import com.nec.strudel.workload.cluster.Node;
import com.nec.strudel.workload.exec.Report;
import com.nec.strudel.workload.job.WorkRequest;
import com.nec.strudel.workload.server.rest.client.RestWorkerServiceProvider;
import com.nec.strudel.workload.worker.Worker;

public class WorkerClient {
	private final WorkerServiceProvider sp;
	public WorkerClient(WorkerServiceProvider sp) {
		this.sp = sp;
	}
	public WorkerClient() {
		this(new RestWorkerServiceProvider());
	}

	public Worker create(WorkRequest work) {
		Node nodeXML = work.getNode();
		WorkerService service = sp.create(nodeXML);
		WorkStatus stat = service.init(work);
		return new RemoteWorker(nodeXML.getUrl(),
				stat.getWorkId(), service);
	}

	protected static class RemoteWorker implements Worker {
		private final String url;
		private final String workId;
		private final WorkerService service;
		protected RemoteWorker(String url,
				String id, WorkerService service) {
			this.url = url;
			this.workId = id;
			this.service = service;
		}

		@Override
		public String getWorkId() {
			return url + "#" + workId;
		}

		@Override
		public String getState() {
			WorkStatus stat = service.getStatus(workId);
			return stat.getState();
		}
		@Override
		public void start() {
			service.start(workId);
		}

		@Override
		public void operate(String name, JsonObject arg) {
			service.operate(workId, name, arg);
		}

		@Override
		public void stop() {
			service.stop(workId);
		}

		@Override
		public void terminate() throws InterruptedException {
			service.terminate(workId);
		}

		@Override
		public Report getReport() {
			return Report.toReport(service.getReport(workId));
		}
	}
}
