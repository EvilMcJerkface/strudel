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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.nec.strudel.exceptions.WorkloadException;
import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.session.Result;
import com.nec.strudel.target.Target;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.workload.job.ConfigParam;
import com.nec.strudel.workload.session.SessionContainer;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.session.SessionProfiler;
import com.nec.strudel.workload.session.StateFactory;

public class UserPoolClosedSessionRunner<T>
        extends AbstractSessionRunner<T> {
    public static final String TAG_NEW_USER_RATIO = "NewUserRatio";
    public static final String SESSION_CONCURRENCY = "SessionConcurrency";
    private static final Logger LOGGER = Logger
            .getLogger(UserPoolClosedSessionRunner.class);
    private final List<SessionContainer<T>> pool = new ArrayList<SessionContainer<T>>();
    private final int size;
    private final RandomSelector<Boolean> useNew;
    private final Random rand;

    public UserPoolClosedSessionRunner(int id,
            SessionFactory<T> sfactory,
            Instrumented<T> con,
            Target<T> target, StateFactory states,
            Instrumented<? extends SessionProfiler> prof, ConfigParam param) {
        super(id, sfactory, con, target, states, prof);
        this.rand = states.getRandom();
        double ratio = param.getDouble(TAG_NEW_USER_RATIO, 0);
        this.useNew = RandomSelector.createBoolean(ratio);
        this.size = param.getInt(SESSION_CONCURRENCY, 1);
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
        return size;
    }

    private void runAction(SessionContainer<T> sc) {
        String name = sc.nextName();
        try {
            Result result = doAction(sc);
            inspectResult(name, result);
            actionDone(sc);
        } catch (WorkloadException ex) {
            int id = getId();
            LOGGER.debug("one session failed @" + id);
            LOGGER.error("session failed due to exception", ex);
        }

    }

    private void actionDone(SessionContainer<T> sc) {
        if (!sc.isActive()) {
            pool.remove(sc);
        }
    }

    private void created(SessionContainer<T> sc) {
        if (pool.size() < size) {
            pool.add(sc);
        } else {
            int idx = rand.nextInt(pool.size());
            pool.set(idx, sc);
        }
    }

    private boolean useNewSession() {
        return pool.size() < size || useNew.next(rand);
    }

    private SessionContainer<T> chooseExisting() {
        if (pool.isEmpty()) {
            return null;
        }
        int idx = rand.nextInt(pool.size());
        return pool.get(idx);
    }

    private SessionContainer<T> nextSession() {
        if (useNewSession()) {
            SessionContainer<T> next = newSession();
            created(next);
            return next;
        } else {
            return chooseExisting();
        }
    }

}
