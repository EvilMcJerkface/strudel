package com.nec.strudel.workload.session.runner;


import java.util.Random;

import javax.annotation.concurrent.NotThreadSafe;

import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.param.ParamConfig;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.exec.WorkExec;
import com.nec.strudel.workload.job.WorkNodeInfo;
import com.nec.strudel.workload.out.Output;
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