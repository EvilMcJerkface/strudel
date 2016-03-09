/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
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

package com.nec.strudel.tkvs.store.impl;

import java.util.Properties;

import com.nec.strudel.instrument.InstrumentUtil;
import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.instrument.Profiling;
import com.nec.strudel.target.Target;
import com.nec.strudel.tkvs.TransactionalDB;
import com.nec.strudel.tkvs.impl.TransactionProfiler;
import com.nec.strudel.tkvs.impl.TransactionalDbServer;
import com.nec.strudel.tkvs.store.TransactionalStore;

@Profiling(TransactionProfilerImpl.class)
public abstract class AbstractTransactionalStore implements TransactionalStore {

    public abstract TransactionalDbServer<TransactionProfiler> createDbServer(
            String dbName, Properties props);

    @Override
    public Target<TransactionalDB> create(String dbName, Properties props) {
        return new DbServer(dbName, createDbServer(dbName, props));
    }

    static class DbServer implements Target<TransactionalDB> {
        private final String dbName;
        private final TransactionalDbServer<TransactionProfiler> dbs;

        public DbServer(String dbName,
                TransactionalDbServer<TransactionProfiler> dbs) {
            this.dbName = dbName;
            this.dbs = dbs;
        }

        @Override
        public TransactionalDB open() {
            return dbs.open(TransactionProfiler.NO_PROF);
        }

        @Override
        public Instrumented<TransactionalDB> open(ProfilerService profs) {
            Instrumented<TransactionProfilerImpl> prof = profs.createProfiler(
                    TransactionProfilerImpl.class,
                    TransactionStat.create(dbName, profs));
            return InstrumentUtil.profiled(dbs.open(prof.getObject()),
                    prof.getProfiler());
        }

        @Override
        public void beginUse(TransactionalDB target) {
            // nothing to do
        }

        @Override
        public void endUse(TransactionalDB target) {
            // nothing to do
        }

        @Override
        public void close() {
            dbs.close();
        }
    }
}
