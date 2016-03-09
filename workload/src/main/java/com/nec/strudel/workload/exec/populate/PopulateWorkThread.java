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

package com.nec.strudel.workload.exec.populate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.metrics.Report;
import com.nec.strudel.target.Target;
import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.exec.batch.WorkThread;
import com.nec.strudel.workload.populator.PopulateProfiler;

public class PopulateWorkThread<T, P> implements WorkThread {
    private static final Logger LOGGER = Logger
            .getLogger(PopulateWorkThread.class);
    public static final int DELAYED_VALIDATION_SIZE = 2000;
    public static final int MAX_WARNS_PER_THREAD = 10;
    private final int id;
    private final PopulatePool<T, P> pool;
    private final Random rand;
    private boolean validate = false;
    private boolean delayedcheck = true;
    private final T con;
    private final List<P> paramBuffer;
    private LoggingValidateReporter reporter;
    private int buffSize;
    private volatile boolean running = false;
    private volatile boolean done = false;
    private volatile boolean success = false;
    private final PopulateProfiler prof;
    private final Target<T> target;

    public PopulateWorkThread(int id, PopulatePool<T, P> pool,
            Instrumented<T> con,
            Target<T> target, PopulateProfiler prof,
            Random rand, boolean validate) {
        this(id, pool, con.getObject(), target,
                prof, rand, validate, LOGGER);
    }

    public PopulateWorkThread(int id, PopulatePool<T, P> pool,
            T con, Target<T> target, PopulateProfiler prof,
            Random rand, boolean validate,
            final Logger logger) {
        this.id = id;
        this.pool = pool;
        this.rand = rand;
        this.con = con;
        this.target = target;
        this.prof = prof;
        this.reporter = new LoggingValidateReporter(MAX_WARNS_PER_THREAD,
                logger);
        this.buffSize = DELAYED_VALIDATION_SIZE;
        this.validate = validate;
        if (validate) {
            paramBuffer = new ArrayList<P>(buffSize);
        } else {
            paramBuffer = new ArrayList<P>(0);
        }
    }

    @Override
    public void run() {
        running = true;
        try {
            Populator<T, P> pop = pool.getPopulator();
            for (PopulateParam param = pool
                    .next(rand); param != null; param = pool.next(rand)) {
                P paramValue = pop.createParameter(param);
                target.beginUse(con);
                prof.start();
                pop.process(con, paramValue);
                prof.end();
                target.endUse(con);
                if (validate) {
                    pop.validate(con, paramValue, reporter);
                    if (delayedcheck) {
                        flushValidateBufferIfFull();
                        paramBuffer.add(paramValue);
                    }
                }
                if (!running) {
                    success = false;
                    return;
                }
            }
            if (validate && delayedcheck) {
                flushValidateBuffer();
            }
            success = true;
        } finally {
            running = false;
            done = true;
        }
    }

    /**
     * TODO support double-check mode by calling this:
     */
    public void setDelayedCheck(boolean doublecheck) {
        this.delayedcheck = doublecheck;
    }

    protected void flushValidateBufferIfFull() {
        if (paramBuffer.size() >= buffSize) {
            flushValidateBuffer();
        }
    }

    protected void flushValidateBuffer() {
        Populator<T, P> pop = pool.getPopulator();
        reporter.setDelayedCheck(true);
        for (P p : paramBuffer) {
            pop.validate(con, p, reporter);
        }
        reporter.setDelayedCheck(false);
        paramBuffer.clear();
    }

    @Override
    public Report getReport() {
        /**
         * TODO report other metrics
         */
        if (reporter.hasWarn()) {
            return Report.warn(reporter.getWarns());
        } else {
            return Report.none();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public boolean isSuccessful() {
        return success;
    }
}
