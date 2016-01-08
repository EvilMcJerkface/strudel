package com.nec.strudel.workload.session.runner.event;

import com.nec.strudel.session.Result;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.session.SessionContainer;
import com.nec.strudel.workload.util.event.TimedEvent;

public class SessionEvent<T> implements TimedEvent<ActionResult<T>> {
	private final long time;
    private final Target<T> target;
	private final SessionContainer<T> sc;
    private final SessionContext<T> ctxt;

	public SessionEvent(SessionContainer<T> sc,
			SessionContext<T> ctxt) {
		this.time = sc.nextTime();
		this.sc = sc;
		this.target = ctxt.target();
		this.ctxt = ctxt;
	}

	@Override
	public ActionResult<T> call() {
    	T c = ctxt.getConnection();
    	target.beginUse(c);
    	try {
    		String name = sc.nextName();
            Result res = sc.doAction(ctxt.profiler(), c);
            ctxt.inspectResult(name, res);
    		return new ActionResult<T>(sc, ctxt, res);
    	} finally {
    		target.endUse(c);
    	}
	}

	@Override
	public long getTime() {
		return time;
	}

	public SessionContainer<T> getContainer() {
		return sc;
	}
	public SessionContext<T> getContext() {
		return ctxt;
	}


}
