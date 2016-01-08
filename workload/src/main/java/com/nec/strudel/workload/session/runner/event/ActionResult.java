package com.nec.strudel.workload.session.runner.event;
import javax.annotation.Nullable;

import com.nec.strudel.session.Result;
import com.nec.strudel.workload.session.SessionContainer;

public class ActionResult<T> {
	private final SessionContainer<T> sc;
    private final SessionContext<T> ctxt;
	private final Result res;

	public ActionResult(SessionContainer<T> sc, SessionContext<T> ctxt,
			Result res) {
		this.sc = sc;
		this.ctxt = ctxt;
		this.res = res;
	}

	@Nullable
	public SessionEvent<T> nextAction() {
		if (sc.isActive()) {
			return new SessionEvent<T>(sc, ctxt);
		}
		return null;
	}

	public SessionEvent<T> newSession(SessionContainer<T> sc) {
		ctxt.profiler().newSession();
		return new SessionEvent<T>(sc, ctxt);
	}

	public Result getResult() {
		return res;
	}

}
