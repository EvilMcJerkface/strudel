package com.nec.strudel.workload.exec;

import javax.annotation.concurrent.NotThreadSafe;

import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.workload.job.ConfigParam;
import com.nec.strudel.workload.job.WorkConfig;
import com.nec.strudel.workload.out.Output;
import com.nec.strudel.workload.state.WorkState;

@NotThreadSafe
public interface Workload {

	WorkExec createWorkExec(WorkConfig conf,
			WorkState state,
			ProfilerService profs);

	Output output(ConfigParam param);

}
