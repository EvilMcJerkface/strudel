package com.nec.strudel.workload.session.runner.event;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.json.JsonObject;

import com.nec.strudel.Closeable;
import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.param.ParamConfig;
import com.nec.strudel.param.ParamSequence;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.exec.Report;
import com.nec.strudel.workload.exec.WorkExec;
import com.nec.strudel.workload.job.WorkNodeInfo;
import com.nec.strudel.workload.out.Output;
import com.nec.strudel.workload.session.SessionConfig;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.session.SessionProfiler;
import com.nec.strudel.workload.session.SessionProfilerImpl;
import com.nec.strudel.workload.session.SessionRunnerStat;
import com.nec.strudel.workload.session.StateFactory;
import com.nec.strudel.workload.session.runner.SessionExecFactory;
import com.nec.strudel.workload.session.runner.SessionProfilerServer;
import com.nec.strudel.workload.session.runner.SessionStatMonitor;
import com.nec.strudel.workload.state.WorkState;
import com.nec.strudel.workload.util.TimeValue;
import com.nec.strudel.workload.util.event.EventExecutor;

public class SemiOpenSessionExecFactory<T> implements SessionExecFactory<T> {
	public static final String TYPE = "semi_open";
	private int sessionConcurrency;


	@Override
	public void initialize(SessionConfig<T> conf) {
		this.sessionConcurrency = conf.getSessionConcurrency();
	}

	@Override
	public WorkExec create(WorkNodeInfo node, Target<T> target,
			SessionFactory<T> sfactory, WorkState state, ProfilerService profs,
			ParamConfig pconf, Random rand) {

		int sessionConcurrency = sessionConcurrency(node);
		SessionProfilerManager sps =
				new SessionProfilerManager(profs,
						sessionConcurrency, node.numOfThreads());

		SessionEventSeq<T> eventSeq = new SessionEventSeq<T>(sfactory,
				createStateFactory(pconf, node, rand),
				sessionConcurrency,
				target, sps, profs, rand);
				
		return new SemiOpenSessionExec<T>(eventSeq,
				node.numOfThreads(), state, target);
	}

	private StateFactory createStateFactory(ParamConfig pconf,
			WorkNodeInfo node, Random rand) {
		ParamSequence seq = pconf.createParamSeq(node.getNodeId(), node.getNodeNum());
		return new StateFactory(seq, new Random(rand.nextLong()));
	}

	private int sessionConcurrency(WorkNodeInfo node) {
		if (sessionConcurrency > 0) {
			return sessionConcurrency;
		} else {
			return node.numOfThreads();
		}
	}

	@Override
	public Output output(SessionConfig<T> xml) {
		return SessionProfilerImpl.output();
	}
	@Override
	public String getType() {
		return TYPE;
	}

	static class SessionProfilerManager implements SessionProfilerServer {
		private final ProfilerService profs;
		private final SessionStatMonitor mon;

		public SessionProfilerManager(ProfilerService profs,
				int sessionConcurrency, int numOfThreads) {
			this.profs = profs;
			this.mon = new SessionStatMonitor();
			profs.register(
			new SessionRunnerStat(sessionConcurrency, numOfThreads, mon));
		}

		@Override
		public Instrumented<? extends SessionProfiler> profiler() {
			Instrumented<SessionProfilerImpl> instr =
					profs.createProfiler(
							SessionProfilerImpl.class);
			instr.getObject().setMon(mon);
			return instr;
		}
	}

	public static class SemiOpenSessionExec<T> extends WorkExec {
		private final EventExecutor<ActionResult<T>> executor;
		private final SessionEventSeq<T> eventSeq;
		private final WorkState state;

		public SemiOpenSessionExec(SessionEventSeq<T> eventSeq,
				int numOfThreads, WorkState state, Closeable... closeables) {
			super(numOfThreads, closeables);
			this.eventSeq = eventSeq;
			this.executor = new EventExecutor<ActionResult<T>>(
					eventSeq, numOfThreads);
			this.state = state;
		}

		@Override
		public String getState() {
			if (executor.hasFailure()) {
				state.fail();
			}
			return state.getState();
		}

		@Override
		public Report getReport() {
			return eventSeq.getReport();
		}

		@Override
		public void start(TimeValue slack) {
			if (state.start()) {
				eventSeq.setStartSlack(slack);
				executor.start();
			}
			
		}

		@Override
		public void operate(String name, JsonObject data) {
			state.operate(name, data);
		}

		@Override
		public void stop() {
			if (state.stop()) {
				executor.stop();
			}
		}

		@Override
		public boolean terminate(long timeout, TimeUnit unit)
				throws InterruptedException {
			if (state.isRunning()) {
				stop();
			}
			if (state.terminate()) {
				return executor.awaitTermination(timeout, unit);
			} else {
				return true;
			}
		}
		
	}
}
