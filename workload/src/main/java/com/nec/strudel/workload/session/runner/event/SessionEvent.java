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
package com.nec.strudel.workload.session.runner.event;

import com.nec.strudel.session.Result;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.exec.event.TimedEvent;
import com.nec.strudel.workload.session.SessionContainer;

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
