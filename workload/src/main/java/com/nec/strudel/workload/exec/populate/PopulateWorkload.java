package com.nec.strudel.workload.exec.populate;

import org.apache.log4j.Logger;

import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.instrument.impl.ProfilerServiceImpl;
import com.nec.strudel.target.Target;
import com.nec.strudel.target.impl.TargetFactory;
import com.nec.strudel.workload.exec.WorkExec;
import com.nec.strudel.workload.exec.Workload;
import com.nec.strudel.workload.exec.batch.BatchExec;
import com.nec.strudel.workload.exec.batch.WorkThread;
import com.nec.strudel.workload.job.ConfigParam;
import com.nec.strudel.workload.job.PopulateWorkItem;
import com.nec.strudel.workload.job.ThreadIds;
import com.nec.strudel.workload.job.WorkConfig;
import com.nec.strudel.workload.out.Output;
import com.nec.strudel.workload.populator.PopulateStat;
import com.nec.strudel.workload.state.WorkState;

public class PopulateWorkload implements Workload {
	private static final Logger LOGGER =
		    Logger.getLogger(PopulateWorkload.class);
	public PopulateWorkload() {
	}

	@Override
	public WorkExec createWorkExec(WorkConfig conf, WorkState state,
			ProfilerService profs) {
		Target<?> store = TargetFactory.create(
				conf.getTargetConfig());
		WorkThread[] works = createWorkThreads(
				(PopulateWorkItem) conf.getItem(),
				conf.getNodeId(), conf.getNodeNum(),
				profs, store);
		return BatchExec.create(state, works, store);
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static WorkThread[] createWorkThreads(PopulateWorkItem item,
			int nodeId, int nodeNum,
			ProfilerService profs,
			Target<?> store) {
		PopulatePool pool =
				PopulatePool.create(item, nodeId, nodeNum);
		LOGGER.info("populate task (node "
				+ nodeId + "/" + nodeNum + "): "
				+ pool.getName() + "["
				+ pool.minId() + ","
				+ pool.maxId() + ")");
		int numOfThreads = item.numOfThreads();
		PopulateStat stat =
				profs.getOrRegister(
					new PopulateStat(pool.getName(), numOfThreads, profs));
		WorkThread[] list = new WorkThread[numOfThreads];
		ThreadIds idGen = new ThreadIds(nodeId);
		for (int i = 0; i < numOfThreads; i++) {
			int id = idGen.idOf(i);
			list[i] = new PopulateWorkThread(id, pool,
					store.open(profs),
					store,
					stat.profiler(),
					item.getRandom(),
					item.isValidate());
		}

		return list;
	}

	public static WorkThread[] createWorkThreads(PopulateWorkItem item,
			Target<?> store) {
		ProfilerServiceImpl profs = ProfilerServiceImpl.noService();
		return createWorkThreads(item, 0, 0, profs, store);
	}


	@Override
	public Output output(ConfigParam param) {
		return Output.empty();
	}


}
