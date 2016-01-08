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

import org.apache.log4j.Logger;

import com.nec.strudel.exceptions.WorkloadException;
import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.session.Result;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.session.SessionContainer;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.session.SessionProfiler;
import com.nec.strudel.workload.session.StateFactory;

/**
 * A session runner that runs sessions one by one: a new session is
 * created when the current session ends. The runner sleeps during the
 * current session's think time.
 * @author tatemura
 *
 * @param <T>
 */
public class SimpleClosedSessionRunner<T> extends AbstractSessionRunner<T>
implements RunnerStat {
    private static final Logger LOGGER =
    		Logger.getLogger(SimpleClosedSessionRunner.class);
	public SimpleClosedSessionRunner(
            int id,
            SessionFactory<T> sfactory,
            Instrumented<T> con,
            Target<T> target,
            StateFactory states, Instrumented<? extends SessionProfiler> prof) {
	    super(id, sfactory, con, target, states, prof);
    }


	@Override
	public void runSessions() {
		for (SessionContainer<T> sc = newSession();
				isRunning() && sc != null; sc = newSession()) {
			try {
				runSession(sc);
			} catch (WorkloadException e) {
			    int id = getId();
				LOGGER.debug("END SESSION (FAIL) @" + id);
				LOGGER.error(
				"session failed due to exception", e);
			}
		}
	}
	@Override
	public int getSessionConcurrency() {
		return 1;
	}

	private void runSession(SessionContainer<T> sc) {
	    int id = getId();
		LOGGER.debug("START SESSION @" + id);
		while (sc.isActive() && isRunning()) {
		    waitTime(sc.waitTime());
		    String name = sc.nextName();
            LOGGER.debug("start: " + name + "@" + id);
		    Result r = doAction(sc);
            LOGGER.debug("end: " + name
            		+ "@" + id);
            inspectResult(name, r);
		}
        LOGGER.debug("END SESSION @" + id);
	}
}
