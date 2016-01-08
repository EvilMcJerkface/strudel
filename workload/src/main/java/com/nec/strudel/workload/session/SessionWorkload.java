package com.nec.strudel.workload.session;

import org.apache.log4j.Logger;

import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.target.Target;
import com.nec.strudel.target.impl.TargetFactory;
import com.nec.strudel.workload.exec.WorkExec;
import com.nec.strudel.workload.exec.Workload;
import com.nec.strudel.workload.job.ConfigParam;
import com.nec.strudel.workload.job.WorkConfig;
import com.nec.strudel.workload.out.Output;
import com.nec.strudel.workload.session.runner.SessionExecFactory;
import com.nec.strudel.workload.state.WorkState;

public class SessionWorkload<T> implements Workload {
	private static final Logger LOGGER =
			Logger.getLogger(SessionWorkload.class);

	@Override
	public WorkExec createWorkExec(WorkConfig conf,
			WorkState state,
			ProfilerService profs) {
		SessionConfig<T> sessionConf = SessionConfig.create(
				conf.getParam());
		SessionFactory<T> sfactory =
				sessionConf.createSessionFactory(conf.getClassPath());
		SessionExecFactory<T> rfactory =
				sessionConf.getSessionExecFactory(
						conf.getClassPath());

		Target<T> store = TargetFactory.create(
				conf.getTargetConfig());

		LOGGER.info("session workload: execution type: "
				+ rfactory.getType());
		return rfactory.create(conf,
				store, sfactory, state, profs,
				sessionConf.getParams(),
				conf.getRandom());
	}

	@Override
	public Output output(ConfigParam param) {
		SessionConfig<T> sessionConf =
				SessionConfig.create(param);
		String classPath = ""; //TODO FIXME
		return sessionConf.output(classPath);
	}

}
