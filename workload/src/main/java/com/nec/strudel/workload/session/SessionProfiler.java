package com.nec.strudel.workload.session;

import javax.annotation.concurrent.NotThreadSafe;

import com.nec.strudel.session.Result;

@NotThreadSafe
public interface SessionProfiler {

	void newSession();

	void startInteraction(String name);

	void finishInteraction(Result result);

}
