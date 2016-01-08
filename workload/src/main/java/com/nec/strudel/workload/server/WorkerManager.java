package com.nec.strudel.workload.server;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.json.JsonObject;

import org.apache.log4j.Logger;

import com.nec.strudel.workload.exec.Report;
import com.nec.strudel.workload.job.WorkRequest;
import com.nec.strudel.workload.worker.LocalWorker;
import com.nec.strudel.workload.worker.Worker;

public class WorkerManager implements WorkerService {
	private static final Logger LOGGER =
		    Logger.getLogger(WorkerManager.class);
	private final AtomicLong idCounter = new AtomicLong();
	private final ConcurrentMap<String, Worker> workers =
			new ConcurrentHashMap<String, Worker>();
	@Override
	public WorkStatus init(WorkRequest work) {
		String id = Long.toHexString(idCounter.getAndIncrement());
		LOGGER.info("creating work: id=" + id);
		try {
			Worker w = LocalWorker.create(id, work);
			workers.put(id, w);
			return WorkStatus.stat(id, w.getState());
		} catch (Throwable e) {
			LOGGER.error("init failed (id=" + id + ")", e);
			return WorkStatus.error(
					"exception during init:" + e.getClass()
					+ ":" + e.getMessage());
		}
	}

	@Override
	public WorkStatus start(String workId) {
		Worker w = workers.get(workId);
		if (w != null) {
			try {
				w.start();
				LOGGER.info("start: " + workId);
				return WorkStatus.stat(workId, w.getState());
			} catch (Throwable e) {
				LOGGER.error("worker start failed:id="
						+ w.getWorkId()
						+ " state=" + w.getState(), e);
				return WorkStatus.error(workId, e.getMessage());
			}
		} else {
			LOGGER.warn("unknown work to start: " + workId);
			return WorkStatus.unknown(workId);
		}
	}

	@Override
	public WorkStatus operate(String workId, String name,
			JsonObject arg) {
		Worker w = workers.get(workId);
		if (w != null) {
			try {
				w.operate(name, arg);
				return WorkStatus.stat(workId, w.getState());
			} catch (Throwable e) {
				LOGGER.error("worker operate failed:id="
						+ w.getWorkId()
						+ " state=" + w.getState()
						+ " op=" + name, e);
				return WorkStatus.error(workId, e.getMessage());
			}
		} else {
			LOGGER.warn("unknown work to operate "
					+ name + ": " + workId);
			return WorkStatus.unknown(workId);
		}
	}

	@Override
	public WorkStatus stop(String workId) {
		Worker w = workers.get(workId);
		if (w != null) {
			w.stop();
		} else {
			LOGGER.warn("unknown work to stop: " + workId);
		}
		return WorkStatus.stat(workId, w.getState());
	}

	@Override
	public WorkStatus getStatus(String workId) {
		Worker w = workers.get(workId);
		if (w != null) {
			return WorkStatus.stat(workId, w.getState());
		} else {
			return WorkStatus.unknown(workId);
		}
	}
	@Override
	public JsonObject getReport(String workId) {
		Worker w = workers.get(workId);
		if (w != null) {
			return w.getReport().toJson();
		} else {
			LOGGER.warn("unknown work to report: " + workId);
			return Report.none().toJson();
		}
	}

	@Override
	public WorkStatus terminate(String workId) throws InterruptedException {
		Worker w = workers.get(workId);
		if (w != null) {
			try {
				w.terminate();
			} catch (Throwable e) {
				LOGGER.error("worker terminate failed:id="
						+ w.getWorkId()
						+ " state=" + w.getState(), e);
				return WorkStatus.error(workId, e.getMessage());
			} finally {
				workers.remove(workId);
			}
			
		} else {
			LOGGER.warn("unknown work to terminate: " + workId);
		}
		return WorkStatus.stat(workId, w.getState());
	}
	@Override
	public Set<String> works() {
		return Collections.unmodifiableSet(workers.keySet());
	}
}
