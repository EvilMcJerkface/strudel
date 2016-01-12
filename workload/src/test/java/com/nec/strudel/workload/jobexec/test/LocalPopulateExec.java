package com.nec.strudel.workload.jobexec.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.nec.strudel.exceptions.WorkloadException;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.exec.batch.WorkThread;
import com.nec.strudel.workload.exec.populate.PopulateWorkload;
import com.nec.strudel.workload.job.PopulateWorkItem;
import com.nec.strudel.workload.jobexec.PopulateRunner.PopulateExec;

public class LocalPopulateExec<T> implements PopulateExec {
	private final ExecutorService exec =
			Executors.newCachedThreadPool();
	private final Target<T> store;
	public LocalPopulateExec(Target<T> store) {
		this.store = store;
	}
	@Override
	public void execute(PopulateWorkItem item) {
		WorkThread[] works =
				PopulateWorkload.createWorkThreads(item, store);
		List<Future<?>> results =
			new ArrayList<Future<?>>(works.length);
		/**
		 * NOTE a set of PopulateWorker threads
		 * share the same task, which will provide
		 * unique parameters to them.
		 */
		for (int i = 0; i < works.length; i++) {
			Future<?> res = exec.submit(works[i]);
			results.add(res);
		}
		ExecutionException ex = null;
		for (Future<?> r : results) {
			try {
				r.get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			} catch (ExecutionException e) {
				ex = e;
			}
		}
		if (ex != null) {
			/**
			 * NOTE throws an exception after everything
			 * is terminated.
			 */
			throw new WorkloadException(
			"Populator execution failed for task "
				+ item.getName(),
					ex.getCause());
		}
	}
	@Override
	public void close() {
		exec.shutdown();
		store.close();
	}
}