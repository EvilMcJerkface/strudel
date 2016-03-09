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

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.session.Interaction;

@ThreadSafe
public abstract class SessionFactory<T> {

    public abstract Session<T> create();

    public static class Builder<T> {
        private int maxCount = 0;
        private int maxTime = 0;
        private int minTime = 0;
        private WaitTime wait = new WaitTime.Builder().build();
        private MarkovStateModel<T> msm;
        private Map<String, Interaction<T>> interactions = new HashMap<String, Interaction<T>>();
        private Map<String, Double> prob;

        public Builder<T> maxCount(int maxCount) {
            this.maxCount = maxCount;
            return this;
        }

        public Builder<T> maxTime(int maxTime) {
            this.maxTime = maxTime;
            return this;
        }

        public Builder<T> minTime(int minTime) {
            this.minTime = minTime;
            return this;
        }

        public Builder<T> uniform(
                Map<String, Interaction<T>> interactions) {
            this.interactions.putAll(interactions);
            return this;
        }

        public Builder<T> random(
                Map<String, Interaction<T>> interactions,
                Map<String, Double> prob) {
            this.interactions.putAll(interactions);
            this.prob = prob;
            return this;
        }

        public Builder<T> states(MarkovStateModel<T> msm) {
            this.msm = msm;
            return this;
        }

        public Builder<T> waitTime(WaitTime wait) {
            this.wait = wait;
            return this;
        }

        public SessionFactory<T> build() {
            if (msm != null) {
                return new MarkovSessionFactory<T>(
                        maxCount, maxTime, minTime,
                        msm, wait);
            } else if (prob != null) {
                return new RandomSessionFactory<T>(
                        maxCount, maxTime,
                        interactions, prob, wait);
            } else {
                return new UniformSessionFactory<T>(
                        maxCount, maxTime,
                        interactions, wait);
            }
        }
    }

    public static class RandomSessionFactory<T> extends SessionFactory<T> {
        private final int maxCount;
        private final long maxTime;
        private final Map<String, Interaction<T>> interactions;
        private final Map<String, Double> prob;
        private final WaitTime wait;

        public RandomSessionFactory(int maxCount, long maxTime,
                Map<String, Interaction<T>> interactions,
                Map<String, Double> prob,
                WaitTime wait) {
            this.maxCount = maxCount;
            this.maxTime = maxTime;
            this.interactions = interactions;
            this.prob = prob;
            this.wait = wait;
        }

        @Override
        public Session<T> create() {
            return new RandomSession<T>(
                    maxCount, maxTime,
                    interactions, prob, wait);
        }
    }

    public static class UniformSessionFactory<T> extends SessionFactory<T> {
        private final int maxCount;
        private final long maxTime;
        private final Map<String, Interaction<T>> interactions;
        private final WaitTime wait;

        public UniformSessionFactory(int maxCount, long maxTime,
                Map<String, Interaction<T>> interactions,
                WaitTime wait) {
            this.maxCount = maxCount;
            this.maxTime = maxTime;
            this.interactions = interactions;
            this.wait = wait;
        }

        @Override
        public Session<T> create() {
            return new RandomSession<T>(maxCount, maxTime,
                    interactions, wait);
        }
    }

    public static class MarkovSessionFactory<T> extends SessionFactory<T> {
        private final int maxCount;
        private final long maxTime;
        private final long minTime;
        private final MarkovStateModel<T> msm;
        private final WaitTime wait;

        public MarkovSessionFactory(int maxCount,
                long maxTime, long minTime,
                MarkovStateModel<T> msm, WaitTime wait) {
            this.maxCount = maxCount;
            this.maxTime = maxTime;
            this.minTime = minTime;
            this.msm = msm;
            this.wait = wait;
        }

        @Override
        public Session<T> create() {
            return new MarkovSession<T>(maxCount, maxTime,
                    minTime, msm, wait);
        }
    }

}
