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

import java.util.Comparator;
import java.util.PriorityQueue;

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
 * A session runner that runs multiple sessions in an interleaving manner: while
 * one session is in its think time, it runs other sessions. A new session is
 * created when there is no session ready to run (think time is done).
 * 
 * @author tatemura
 *
 * @param <T>
 *            the type of the database.
 */
public class MultiSessionRunner<T> extends AbstractSessionRunner<T> {
    private static final Logger LOGGER = Logger
            .getLogger(MultiSessionRunner.class);
    private static final int INIT_QUEUE_SIZE = 32;

    private final PriorityQueue<SessionContainer<T>> queue = new PriorityQueue<SessionContainer<T>>(
            INIT_QUEUE_SIZE,
            new Comparator<SessionContainer<T>>() {

                @Override
                public int compare(SessionContainer<T> o1,
                        SessionContainer<T> o2) {
                    return (int) (o1.nextTime() - o2.nextTime());
                }
            });

    public MultiSessionRunner(int id, SessionFactory<T> sfactory,
            Instrumented<T> con,
            Target<T> target,
            StateFactory states,
            Instrumented<? extends SessionProfiler> prof) {
        super(id, sfactory, con, target, states, prof);
    }

    @Override
    public void runSessions() {
        for (SessionContainer<T> sc = nextSession(); isRunning()
                && sc != null; sc = nextSession()) {
            runAction(sc);
        }
    }

    @Override
    public int getSessionConcurrency() {
        return queue.size() + 1;
    }

    private void runAction(SessionContainer<T> sc) {
        int id = getId();
        String name = sc.nextName();
        LOGGER.debug("start: " + name + "@" + id);
        try {
            Result result = doAction(sc);
            LOGGER.debug("end: " + name + "@" + id);
            inspectResult(name, result);
            if (sc.isActive()) {
                queue.add(sc);
            } else {
                LOGGER.debug("one session finished @" + id);
            }
        } catch (WorkloadException ex) {
            LOGGER.debug("one session failed @" + id);
            LOGGER.error("session failed due to exception", ex);
        }

    }

    private SessionContainer<T> nextSession() {
        SessionContainer<T> top = queue.peek();
        if (top != null && top.isReady()) {
            return queue.poll();
        }
        SessionContainer<T> next = newSession();
        if (next == null) {
            if (queue.isEmpty()) {
                return null;
            } else {
                long wait = top.nextTime() - System.currentTimeMillis();
                waitTime(wait);
                return queue.poll();
            }
        }
        return next;
    }

}
