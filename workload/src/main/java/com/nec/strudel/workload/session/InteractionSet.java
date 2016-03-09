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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.nec.congenio.annotation.MapOf;

@MapOf(InteractionConfig.class)
public class InteractionSet {
    public static InteractionSet empty() {
        return new InteractionSet();
    }

    private final Map<String, InteractionConfig> intrs;

    public InteractionSet(Map<String, InteractionConfig> intrs) {
        this.intrs = intrs;
    }

    public InteractionSet() {
        intrs = Collections.emptyMap();
    }

    public Set<String> names() {
        return intrs.keySet();
    }

    public boolean isEmpty() {
        return intrs.isEmpty();
    }

    public double getProb(String name) {
        InteractionConfig conf = intrs.get(name);
        if (conf != null) {
            return conf.getProb();
        } else {
            return 0;
        }
    }

    @Nullable
    public ThinkTime getThinkTime(String name) {
        InteractionConfig conf = intrs.get(name);
        if (conf != null) {
            return conf.getThinkTime();
        } else {
            return null;
        }
    }
}