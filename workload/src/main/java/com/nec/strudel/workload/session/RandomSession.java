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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.impl.State;
import com.nec.strudel.util.RandomSelector;

/**
 * A session that generates a sequence of user actions randomly. The probability
 * of choosing a specific action is independent of the past actions.
 * 
 * @author tatemura
 *
 */
public class RandomSession<T> extends SessionBaseImpl<T> {
    private final Map<String, Interaction<T>> intrs;
    private final RandomSelector<String> selector;
    private final WaitTime wait;

    public RandomSession(int maxCount, long maxTime,
            Map<String, Interaction<T>> intreactions,
            Map<String, Double> prob, WaitTime wait) {
        super(maxCount, maxTime);
        this.intrs = intreactions;
        this.selector = RandomSelector.create(prob);
        this.wait = wait;
    }

    public RandomSession(int maxCount, long maxTime,
            Map<String, Interaction<T>> interactions,
            WaitTime wait) {
        super(maxCount, maxTime);
        this.intrs = interactions;
        List<String> list = new ArrayList<String>(
                interactions.keySet());
        this.selector = RandomSelector.create(list);
        this.wait = wait;
    }

    protected UserAction<T> get(State state) {
        Random rand = state.getRandom();
        String name = selector.next(rand);
        Interaction<T> intr = intrs.get(name);
        if (intr == null) {
            return null;
        } else {
            return new UserAction<T>(name, intr,
                    wait.prepareTime(name, rand),
                    wait.thinkTime(name, rand));
        }
    }

    @Override
    protected UserAction<T> getFirst(State state) {
        return get(state);
    }

    @Override
    protected UserAction<T> getNext(UserAction<T> current, State state) {
        return get(state);
    }

}
