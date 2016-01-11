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
package com.nec.strudel.workload.session.runner;


import java.util.Random;

import javax.annotation.concurrent.NotThreadSafe;

import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.metrics.Output;
import com.nec.strudel.param.ParamConfig;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.exec.WorkExec;
import com.nec.strudel.workload.job.WorkNodeInfo;
import com.nec.strudel.workload.session.SessionConfig;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.state.WorkState;

@NotThreadSafe
public interface SessionExecFactory<T> {

	void initialize(SessionConfig<T> conf);


	WorkExec create(WorkNodeInfo node,
			Target<T> target,
			SessionFactory<T> sfactory, WorkState state, ProfilerService profs,
			ParamConfig pconf, Random rand);

	Output output(SessionConfig<T> conf);

	String getType();
}