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
		public DbServer(String dbName, TransactionalDbServer<TransactionProfiler> dbs) {
			this.dbName = dbName;
			this.dbs = dbs;
		}
		@Override
		public TransactionalDB open() {
			return dbs.open(TransactionProfiler.NO_PROF);
		}
		@Override
		public Instrumented<TransactionalDB> open(ProfilerService profs) {
			Instrumented<TransactionProfilerImpl> p = profs.createProfiler(
					TransactionProfilerImpl.class,
					TransactionStat.create(dbName, profs));
			return InstrumentUtil.profiled(dbs.open(p.getObject()), p.getProfiler());
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
