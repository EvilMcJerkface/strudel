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

import java.util.Random;

import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.metrics.Output;
import com.nec.strudel.param.ParamConfig;
import com.nec.strudel.param.ParamSequence;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.exec.WorkExec;
import com.nec.strudel.workload.exec.event.EventExec;
import com.nec.strudel.workload.job.WorkNodeInfo;
import com.nec.strudel.workload.session.SessionConfig;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.session.SessionProfiler;
import com.nec.strudel.workload.session.SessionProfilerImpl;
import com.nec.strudel.workload.session.SessionRunnerStat;
import com.nec.strudel.workload.session.StateFactory;
import com.nec.strudel.workload.session.runner.SessionExecFactory;
import com.nec.strudel.workload.session.runner.SessionProfilerServer;
import com.nec.strudel.workload.session.runner.SessionStatMonitor;
import com.nec.strudel.workload.state.WorkState;

/**
 * The workload model is "semi-open" in a sense that arrival of new users is not
 * supported: i.e., a new user session starts when one of the session ends --
 * thus the total number of active sessions is kept constant. We call this
 * number "session concurrency." Notice that some of the active sessions might
 * be at their "think" time. The number of sessions that are running
 * interactions can be smaller than the session concurrency.
 * 
 * @author tatemura
 *
 * @param <T>
 */
public class SemiOpenSessionExecFactory<T> implements SessionExecFactory<T> {
    public static final String TYPE = "semi_open";
    private int sessionConcurrency;

    @Override
    public void initialize(SessionConfig<T> conf) {
        this.sessionConcurrency = conf.getSessionConcurrency();
    }

    @Override
    public WorkExec create(WorkNodeInfo node, Target<T> target,
            SessionFactory<T> sfactory, WorkState state, ProfilerService profs,
            ParamConfig pconf, Random rand) {

        int sessionConcurrency = sessionConcurrency(node);
        SessionProfilerManager sps = new SessionProfilerManager(profs,
                sessionConcurrency, node.numOfThreads());

        SessionEventSeq<T> eventSeq = new SessionEventSeq<T>(sfactory,
                createStateFactory(pconf, node, rand),
                sessionConcurrency,
                target, sps, profs, rand);

        return EventExec.create(eventSeq, eventSeq,
                node.numOfThreads(), state, target);
    }

    private StateFactory createStateFactory(ParamConfig pconf,
            WorkNodeInfo node, Random rand) {
        ParamSequence seq = pconf.createParamSeq(node.getNodeId(),
                node.getNodeNum());
        return new StateFactory(seq, new Random(rand.nextLong()));
    }

    private int sessionConcurrency(WorkNodeInfo node) {
        if (sessionConcurrency > 0) {
            return sessionConcurrency;
        } else {
            return node.numOfThreads();
        }
    }

    @Override
    public Output output(SessionConfig<T> xml) {
        return SessionProfilerImpl.output();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    static class SessionProfilerManager implements SessionProfilerServer {
        private final ProfilerService profs;
        private final SessionStatMonitor mon;

        public SessionProfilerManager(ProfilerService profs,
                int sessionConcurrency, int numOfThreads) {
            this.profs = profs;
            this.mon = new SessionStatMonitor();
            profs.register(
                    new SessionRunnerStat(sessionConcurrency, numOfThreads,
                            mon));
        }

        @Override
        public Instrumented<? extends SessionProfiler> profiler() {
            Instrumented<SessionProfilerImpl> instr = profs.createProfiler(
                    SessionProfilerImpl.class);
            instr.getObject().setMon(mon);
            return instr;
        }
    }
}
