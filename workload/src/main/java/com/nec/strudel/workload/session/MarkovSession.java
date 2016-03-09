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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.impl.State;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.workload.session.MarkovStateModel.MarkovState;
import com.nec.strudel.workload.session.MarkovStateModel.Transition;

/**
 * A session that generates a sequence of user actions based on state
 * transition.
 * 
 * @author tatemura
 *
 */
public class MarkovSession<T> extends SessionBaseImpl<T> {

    private final Map<String, RandomSelector<MarkovState<T>>> transMap;
    private final long minTime;
    private final WaitTime wait;

    /**
     * @param maxCount
     *            a positive number specifies the maximum interaction: if the
     *            session generates this number of interactions, the session
     *            goes to END regardless of the transition model.
     * @param maxTime
     *            a positive number specifies the maximum duration: if the
     *            duration becomes longer than this value, the session goes to
     *            END regardless of the transition model.
     * @param minTime
     *            a positive number specifies the User Session Minimum Duration.
     *            If the session reaches END before this duration, the session
     *            generates the next interaction from START.
     * @param msm
     */
    @SuppressWarnings("unchecked")
    public MarkovSession(int maxCount, long maxTime, long minTime,
            MarkovStateModel<T> msm, WaitTime wait) {
        super(maxCount, maxTime);
        this.minTime = minTime;
        this.wait = wait;
        transMap = new HashMap<String, RandomSelector<MarkovState<T>>>();

        for (Transition t : msm.getTransitions()) {
            Map<MarkovState<T>, Double> probs = new HashMap<MarkovState<T>, Double>();

            for (Map.Entry<String, Double> e : t.nextStates().entrySet()) {
                String name = e.getKey();
                if (MarkovStateModel.END.equals(name)) {
                    probs.put(MarkovState.END_STATE,
                            e.getValue());
                } else {
                    MarkovState<T> state = msm.getState(name);
                    if (state == null) {
                        throw new ConfigException(
                                "null state for " + name);
                    }
                    /**
                     * TODO exception if state == null
                     */

                    probs.put(state, e.getValue());
                }
            }
            if (probs.isEmpty()) {
                throw new ConfigException(
                        "empty transition: " + t.getName());
            }
            transMap.put(t.getName(), RandomSelector.create(probs));
        }
    }

    public long getMinTime() {
        return minTime;
    }

    @Override
    protected UserAction<T> getFirst(State state) {
        return findNext(MarkovStateModel.START,
                state.getResultMode(), state.getRandom());
    }

    @Override
    protected UserAction<T> getNext(UserAction<T> current, State state) {
        UserAction<T> action = findNext(
                current.getName(),
                state.getResultMode(),
                state.getRandom());
        if (action == null && minTime > 0) {
            long duration = System.currentTimeMillis() - startTime();
            if (duration < minTime) {
                /**
                 * NOTE it has not reach the User Session Minimum Duration
                 */
                return getFirst(state);
            }
        }
        return action;
    }

    private MarkovState<T> nextState(String name,
            String mode, Random rand) {
        RandomSelector<MarkovState<T>> state = null;
        if (!mode.isEmpty()) {
            state = transMap.get(
                    MarkovStateModel.modifiedState(name, mode));
        }
        if (state == null) {
            state = transMap.get(name);
        }
        if (state == null) {
            // TODO exception
        }
        return state.next(rand);
    }

    private UserAction<T> findNext(String current,
            String mode, Random rand) {
        MarkovState<T> state = nextState(current, mode, rand);
        while (!state.hasInteraction()) {
            state = nextState(state.getName(), "", rand);
        }
        return actionFor(state.getName(), state.getInteraction(), rand);
    }

    UserAction<T> actionFor(String name, Interaction<T> intr, Random rand) {
        if (intr == null) {
            return null;
        }
        return new UserAction<T>(name, intr,
                wait.prepareTime(name, rand),
                wait.thinkTime(name, rand));
    }

}
