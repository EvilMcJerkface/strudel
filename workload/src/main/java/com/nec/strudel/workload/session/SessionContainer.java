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
package com.nec.strudel.workload.session;

import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.impl.ParamBuilderImpl;
import com.nec.strudel.session.impl.State;
import com.nec.strudel.session.impl.StateModifierImpl;


/**
 * A class that encapsulates a session instance and
 * its state.
 * @author tatemura
 *
 * @param <T>
 */
public class SessionContainer<T> {
    private final Session<T> session;
    private final State state;
    private volatile UserAction<T> action;
    private volatile long nextTime;
    private volatile long waitTime;

    public SessionContainer(Session<T> session, State state) {
        this.session = session;
        this.state = state;
        prepareNext();
    }
    public boolean isActive() {
        return action != null;
    }
    public String nextName() {
        if (action != null) {
            return action.getName();
        } else {
            return null;
        }
    }
    public Result doAction(SessionProfiler prof, T db) {
        if (action == null) {
            return new ResultBuilder().failure("INTERNAL_ERROR");
        }
        Interaction<T> intr = action.getInteraction();
        Param param = new Param();
        intr.prepare(new ParamBuilderImpl(state, param));
        prof.startInteraction(action.getName());
        Result r = intr.execute(
                param, db,
                new ResultBuilder());
        prof.finishInteraction(r);
        state.setResultMode(r.getMode());
        intr.complete(new StateModifierImpl(r, state));
        prepareNext();
        return r;
    }

    private void prepareNext() {
        waitTime =
                (action != null ? action.getThinkTime() : 0);
        action = session.next(state);
        if (action != null) {
            waitTime += action.getPrepareTime();
            nextTime = System.currentTimeMillis() + waitTime;
        } else {
            waitTime = 0;
            nextTime = -1;
        }
    }
    public long waitTime() {
        return waitTime;
    }
    public long nextTime() {
        return nextTime;
    }
    public boolean isReady() {
        return nextTime <= System.currentTimeMillis();
    }
    public void delay(long msec) {
        if (nextTime != -1) {
            waitTime += msec;
            nextTime += msec;
        }
    }
}
