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
package com.nec.strudel.workload.session;

import org.apache.log4j.Logger;

import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.metrics.Output;
import com.nec.strudel.target.Target;
import com.nec.strudel.target.impl.TargetFactory;
import com.nec.strudel.workload.exec.WorkExec;
import com.nec.strudel.workload.exec.Workload;
import com.nec.strudel.workload.job.ConfigParam;
import com.nec.strudel.workload.job.WorkConfig;
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
